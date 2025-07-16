// Tax Split endpoints
    @GET("tax-splits")
    suspend fun getAllTaxSplits(): Response<List<TaxSplit>>

    @GET("tax-splits/{id}")
    suspend fun getTaxSplitById(@Path("id") id: Long): Response<TaxSplit>

    @POST("tax-splits")
    suspend fun createTaxSplit(@Body taxSplit: TaxSplit): Response<TaxSplit>

    @PUT("tax-splits/{id}")
    suspend fun updateTaxSplit(@Path("id") id: Long, @Body taxSplit: TaxSplit): Response<TaxSplit>

    @DELETE("tax-splits/{id}")
    suspend fun deleteTaxSplit(@Path("id") id: Long): Response<Unit>

    // Paid Bills endpoints
    @GET("paid-bills")
    suspend fun getAllPaidBills(): Response<List<PaidBillSummary>>

    @GET("paid-bills/{id}")
    suspend fun getPaidBillById(@Path("id") id: Long): Response<PaidBill>

    @PUT("paid-bills/{id}")
    suspend fun updatePaidBill(@Path("id") id: Long, @Body billData: PaidBill): Response<PaidBill>

    @DELETE("paid-bills/{id}")
    suspend fun deletePaidBill(@Path("id") id: Long): Response<Unit>

    @POST("paid-bills/{id}/refund")
    suspend fun refundBill(@Path("id") id: Long, @Body refundData: Map<String, Any>): Response<PaidBill>

    @GET("paid-bills/search")
    suspend fun searchPaidBills(@Query("q") query: String): Response<List<PaidBillSummary>>