package com.warriortech.resb.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.warriortech.resb.model.TblStaff
import androidx.core.content.edit

/**
 * Session manager for handling authentication and user data
 * Uses EncryptedSharedPreferences for secure storage
 */
object SessionManager {
    private const val TAG = "SessionManager"
    private const val PREF_NAME = "ResbPrefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER = "user"
    private const val KEY_COMPANY_CODE = "company_code"
    private const val KEY_LAST_SYNC = "last_sync_timestamp"
    
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()
    
    /**
     * Initialize the session manager with application context
     * Must be called from Application onCreate or a splash activity
     */
    fun init(context: Context) {
        try {
            // Create master key for encryption
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            // Create encrypted shared preferences
            prefs = EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            
            Log.d(TAG, "SessionManager initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing encrypted preferences: ${e.message}")
            // Fall back to regular shared preferences if encryption fails
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }
    
    /**
     * Save authentication token
     */
    fun saveAuthToken(token: String) {
        checkInitialization()
        prefs.edit { putString(KEY_AUTH_TOKEN, token) }
    }
    
    /**
     * Get authentication token
     */
    fun getAuthToken(): String? {
        checkInitialization()
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }
    
    /**
     * Save user data
     */
    fun saveUser(user: TblStaff) {
        checkInitialization()
        val userJson = gson.toJson(user)
        prefs.edit() { putString(KEY_USER, userJson) }
    }
    
    /**
     * Get user data
     */
    fun getUser(): TblStaff? {
        checkInitialization()
        val userJson = prefs.getString(KEY_USER, null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, TblStaff::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing user data: ${e.message}")
                null
            }
        } else {
            null
        }
    }
    
    /**
     * Save company code
     */
    fun saveCompanyCode(companyCode: String) {
        checkInitialization()
        prefs.edit { putString(KEY_COMPANY_CODE, companyCode) }
    }
    
    /**
     * Get company code
     */
    fun getCompanyCode(): String? {
        checkInitialization()
        return prefs.getString(KEY_COMPANY_CODE, null)
    }
    
    /**
     * Update last sync timestamp
     */
    fun updateLastSyncTimestamp() {
        checkInitialization()
        prefs.edit { putLong(KEY_LAST_SYNC, System.currentTimeMillis()) }
    }
    
    /**
     * Get last sync timestamp
     */
    fun getLastSyncTimestamp(): Long {
        checkInitialization()
        return prefs.getLong(KEY_LAST_SYNC, 0)
    }
    
    /**
     * Clear session data on logout
     */
    fun clearSession() {
        checkInitialization()
        prefs.edit { clear() }
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        checkInitialization()
        return getAuthToken() != null && getUser() != null
    }
    
    /**
     * Check if session manager is initialized
     */
    private fun checkInitialization() {
        if (!::prefs.isInitialized) {
            throw IllegalStateException("SessionManager not initialized. Call init() first.")
        }
    }
}