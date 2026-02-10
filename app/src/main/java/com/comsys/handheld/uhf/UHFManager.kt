package com.comsys.handheld.uhf

import android.content.Context
import android.util.Log
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.entity.UHFTAGInfo
import com.rscja.deviceapi.interfaces.IUHF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class UHFManager private constructor() {
    private var uhfReader: RFIDWithUHFUART? = null
    private var isInitialized = false
    private var isScanning = false

    private val _tags = MutableStateFlow<List<TagInfo>>(emptyList())
    val tags: StateFlow<List<TagInfo>> = _tags.asStateFlow()

    private val _status = MutableStateFlow<String>("Not initialized")
    val status: StateFlow<String> = _status.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    companion object {
        private const val TAG = "UHFManager"

        @Volatile
        private var instance: UHFManager? = null

        fun getInstance(): UHFManager {
            return instance ?: synchronized(this) {
                instance ?: UHFManager().also { instance = it }
            }
        }
    }

    suspend fun initialize(context: Context): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (isInitialized) {
                return@withContext Result.success("Already initialized")
            }

            _status.value = "Initializing..."

            // Get the UHF reader instance
            uhfReader = RFIDWithUHFUART.getInstance()

            if (uhfReader == null) {
                _status.value = "Failed to get reader instance"
                return@withContext Result.failure(Exception("Failed to get UHF reader instance"))
            }

            // Initialize the reader
            val initResult = uhfReader!!.init(context)

            if (initResult) {
                isInitialized = true
                _isConnected.value = true
                val version = uhfReader!!.version ?: "Unknown"
                _status.value = "Initialized - Version: $version"
                Log.d(TAG, "UHF Reader initialized successfully")
                Result.success("Reader initialized: $version")
            } else {
                _status.value = "Initialization failed"
                Result.failure(Exception("Failed to initialize UHF reader"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing UHF reader", e)
            _status.value = "Error: ${e.message}"
            Result.failure(e)
        }
    }

    suspend fun startInventory(): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!isInitialized) {
                return@withContext Result.failure(Exception("Reader not initialized"))
            }

            if (isScanning) {
                return@withContext Result.failure(Exception("Already scanning"))
            }

            _tags.value = emptyList()

            // Start inventory
            val result = uhfReader?.startInventoryTag()

            if (result == true) {
                isScanning = true
                _status.value = "Scanning..."

                // Start reading tags in a loop
                readTagsLoop()

                Result.success("Inventory started")
            } else {
                _status.value = "Failed to start inventory"
                Result.failure(Exception("Failed to start inventory"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting inventory", e)
            _status.value = "Error: ${e.message}"
            Result.failure(e)
        }
    }

    private suspend fun readTagsLoop() = withContext(Dispatchers.IO) {
        val seenTags = mutableMapOf<String, TagInfo>()

        while (isScanning) {
            try {
                val tagInfo: UHFTAGInfo? = uhfReader?.readTagFromBuffer()

                if (tagInfo != null) {
                    val epc = tagInfo.epc
                    if (!epc.isNullOrEmpty()) {
                        val tag = TagInfo(
                            epc = epc,
                            tid = tagInfo.tid ?: "",
                            rssi = tagInfo.rssi?.toString() ?: "0",
                            count = (seenTags[epc]?.count ?: 0) + 1,
                            timestamp = System.currentTimeMillis()
                        )

                        seenTags[epc] = tag
                        _tags.value = seenTags.values.sortedByDescending { it.timestamp }
                        _status.value = "Found ${seenTags.size} tags"

                        Log.d(TAG, "Tag read: EPC=$epc, RSSI=${tag.rssi}")
                    }
                }

                // Small delay to prevent overwhelming the CPU
                delay(50)
            } catch (e: Exception) {
                Log.e(TAG, "Error reading tag", e)
            }
        }
    }

    suspend fun stopInventory(): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!isScanning) {
                return@withContext Result.success("Not scanning")
            }

            isScanning = false
            val result = uhfReader?.stopInventory()

            if (result == true) {
                _status.value = "Stopped - ${_tags.value.size} tags found"
                Result.success("Inventory stopped")
            } else {
                _status.value = "Failed to stop inventory"
                Result.failure(Exception("Failed to stop inventory"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping inventory", e)
            _status.value = "Error: ${e.message}"
            Result.failure(e)
        }
    }

    suspend fun readTagData(
        epc: String,
        bank: Int = IUHF.Bank_USER,
        offset: Int = 0,
        length: Int = 4
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!isInitialized) {
                return@withContext Result.failure(Exception("Reader not initialized"))
            }

            val data = uhfReader?.readData(epc, bank, offset, length)

            if (!data.isNullOrEmpty()) {
                Log.d(TAG, "Read data from EPC $epc: $data")
                Result.success(data)
            } else {
                Result.failure(Exception("Failed to read data or no data available"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading tag data", e)
            Result.failure(e)
        }
    }

    suspend fun writeTagData(
        epc: String,
        bank: Int = IUHF.Bank_USER,
        offset: Int = 0,
        length: Int = 4,
        data: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!isInitialized) {
                return@withContext Result.failure(Exception("Reader not initialized"))
            }

            val result = uhfReader?.writeData(epc, bank, offset, length, data)

            if (result == true) {
                Log.d(TAG, "Write data to EPC $epc: $data")
                Result.success("Data written successfully")
            } else {
                Result.failure(Exception("Failed to write data"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error writing tag data", e)
            Result.failure(e)
        }
    }

    fun clearTags() {
        _tags.value = emptyList()
        _status.value = if (isInitialized) "Ready" else "Not initialized"
    }

    suspend fun disconnect(): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (isScanning) {
                stopInventory()
            }

            uhfReader?.free()
            isInitialized = false
            _isConnected.value = false
            _status.value = "Disconnected"
            _tags.value = emptyList()

            Log.d(TAG, "UHF Reader disconnected")
            Result.success("Disconnected")
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting", e)
            Result.failure(e)
        }
    }

    data class TagInfo(
        val epc: String,
        val tid: String = "",
        val rssi: String = "0",
        val count: Int = 1,
        val timestamp: Long = System.currentTimeMillis()
    )
}
