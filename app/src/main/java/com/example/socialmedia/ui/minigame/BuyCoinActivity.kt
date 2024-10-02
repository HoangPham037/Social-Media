package com.example.socialmedia.ui.minigame

import android.annotation.SuppressLint
import android.os.Handler
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.example.socialmedia.R
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.ActivityBuyCoinBinding
import com.example.socialmedia.ui.home.HomeViewModel
import com.example.socialmedia.ui.minigame.base.BaseActivity
import com.example.socialmedia.ui.utils.Constants
import com.google.common.collect.ImmutableList
import org.koin.android.ext.android.inject

class BuyCoinActivity : BaseActivity<ActivityBuyCoinBinding>() {
    private var billingClient: BillingClient? = null
    lateinit var mAdapter: AdapterBuyCoin
    private val listItemHome = arrayListOf<BuyCoin>()
    private var productDetailsList: MutableList<ProductDetails> = arrayListOf()
    private var onPurchaseResponse: OnPurchaseResponse? = null
    private var handler: Handler? = null
    private val viewModel: HomeViewModel by inject()
    override fun layoutId(): Int {
        return R.layout.activity_buy_coin
    }

    override fun bindView() {

        mAdapter = AdapterBuyCoin(this) {
            launchPurchaseFlow(it)
        }
        viewBinding.rcvBuyCoin.adapter = mAdapter
        viewBinding.rcvBuyCoin.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        handler = Handler()
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases()
            .setListener { billingResult: BillingResult?, list: List<Purchase?>? -> }
            .build()
        establishConnection()
    }

    fun establishConnection() {
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun showProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(inAppProductList)
            .build()
        billingClient!!.queryProductDetailsAsync(
            params
        ) { billingResult: BillingResult?, prodDetailsList: List<ProductDetails> ->
            // Process the result
            productDetailsList!!.clear()
            handler!!.postDelayed({

                //                        hideProgressDialog();
                productDetailsList!!.addAll(prodDetailsList)
                mAdapter!!.submitdata(productDetailsList as ArrayList<ProductDetails>)
                if (prodDetailsList.size == 0) Toast.makeText(
                    this@BuyCoinActivity,
                    "prodDetailsList, size = 0",
                    Toast.LENGTH_SHORT
                ).show()
            }, 2000)
        }
    }

    //Product 1
    //Product 2
    //Product 3
    //Product 4
    //Product 5
    //Product 6
    private val inAppProductList: ImmutableList<QueryProductDetailsParams.Product>
        private get() = ImmutableList.of( //Product 1
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(Constants.KEY_5_COIN)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(Constants.KEY_10_COIN)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),  //Product 2
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(Constants.KEY_20_COIN)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),  //Product 3
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(Constants.KEY_50_COIN)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),  //Product 4
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(Constants.KEY_100_COIN)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),  //Product 5
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(Constants.KEY_150_COIN)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),  //Product 6
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(Constants.KEY_200_COIN)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(Constants.KEY_300_COIN)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

    fun verifyInAppPurchase(purchases: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams
            .newBuilder()
            .setPurchaseToken(purchases.purchaseToken)
            .build()
        billingClient!!.acknowledgePurchase(acknowledgePurchaseParams) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val proId = purchases.products[0]
                val quantity = purchases.quantity
                setPurchaseResponse(object : OnPurchaseResponse {
                    override fun onResponse(proId: String?, quantity: Int) {
                        proId?.let {
                            setupResult(
                                it,
                                quantity
                            )
                        }
                    }
                })
                onPurchaseResponse!!.onResponse(proId, quantity)
                allowMultiplePurchases(purchases)
                //                val coinContain =
                //                    MainApp.newInstance()?.preference?.getValueCoin()?.plus(getCoinFromKey(proId))
                //                coinContain?.let { MainApp.newInstance()?.preference?.setValueCoin(it) }
                //                //                Toast.makeText(PurchaseInAppActivity.this, "verifyInAppPurchase Mua ok--> " + proId, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun allowMultiplePurchases(purchase: Purchase) {
        val consumeParams = ConsumeParams
            .newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient!!.consumeAsync(consumeParams) { billingResult, s ->
            Toast.makeText(
                this@BuyCoinActivity,
                " Resume item ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        billingClient!!.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult: BillingResult, list: List<Purchase> ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in list) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                        verifyInAppPurchase(purchase)
                    }
                }
            }
        }
    }


    private fun launchPurchaseFlow(productDetails: ProductDetails) {
        // handle item select
        //        assert productDetails.getSubscriptionOfferDetails() != null;
        val productDetailsParamsList = ImmutableList.of(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        billingClient!!.launchBillingFlow(this, billingFlowParams)
    }

    private fun setupResult(proId: String, quantity: Int) {
        viewModel.getUser(viewModel.repository.getUid()) {
            when (it) {
                is State.Success -> {
                    val remainCoin = it.data.gold + getCoinFromKey(proId) * quantity;
                    viewModel.updateGold(remainCoin){
                        when(it){
                            is State.Success -> Toast.makeText(this , "by gold  Success", Toast.LENGTH_SHORT).show()

                            else -> {

                            }
                        }
                    }
                }

                else -> {
                }
            }
        }
    }


    private fun getCoinFromKey(coinId: String): Int {
        return when (coinId) {
            Constants.KEY_5_COIN -> 2
            Constants.KEY_10_COIN -> 10
            Constants.KEY_20_COIN -> 25
            Constants.KEY_50_COIN -> 40
            Constants.KEY_100_COIN -> 65
            Constants.KEY_150_COIN -> 90
            Constants.KEY_200_COIN -> 125
            Constants.KEY_300_COIN -> 150
            else -> 0
        }
    }

    internal interface OnPurchaseResponse {
        fun onResponse(proId: String?, quantity: Int)
    }

    private fun setPurchaseResponse(onPurchaseResponse: OnPurchaseResponse) {
        this.onPurchaseResponse = onPurchaseResponse
    }

    override fun observeData() {

    }

}