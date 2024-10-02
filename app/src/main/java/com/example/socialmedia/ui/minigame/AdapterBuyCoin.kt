package com.example.socialmedia.ui.minigame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.example.socialmedia.R
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.ui.utils.Constants

class AdapterBuyCoin(val context: Context, val onClickItem: (ProductDetails) -> Unit) :
    RecyclerView.Adapter<AdapterBuyCoin.ViewHolder>() {

    var list = listOf<ProductDetails>()

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val tvcoin = item.findViewById<TextView>(R.id.txtnumcoin)
        val btn = item.findViewById<AppCompatButton>(R.id.btnBuy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterBuyCoin.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_for_buy_coin_rcv, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AdapterBuyCoin.ViewHolder, position: Int) {
        val productDetails = list[position]
        val price: String? = productDetails.getOneTimePurchaseOfferDetails()?.getFormattedPrice()
        holder.btn.text = price
        holder.tvcoin.setText(setTitleValue(productDetails.getProductId()))
        holder.itemView.click {
            onClickItem.invoke(productDetails)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun submitdata(listdata: ArrayList<ProductDetails>) {
        list = listdata
    }

    private fun setTitleValue(productId: String): String {
        return when (productId) {

            Constants.KEY_5_COIN -> String.format(
                context.resources.getString(R.string.message_purchase_one),
                "2 coin"
            )

            Constants.KEY_10_COIN -> String.format(
                context.resources.getString(R.string.message_purchase_one),
                "10 coin"
            )

            Constants.KEY_20_COIN -> String.format(
                context.resources.getString(R.string.message_purchase_one),
                "25 coin"
            )

            Constants.KEY_50_COIN -> String.format(
                context.resources.getString(R.string.message_purchase_one),
                "40 coin"
            )

            Constants.KEY_100_COIN -> String.format(
                context.resources.getString(R.string.message_purchase_one),
                "65 coin"
            )

            Constants.KEY_150_COIN -> String.format(
                context.resources.getString(R.string.message_purchase_one),
                "90 coin"
            )

            Constants.KEY_200_COIN -> String.format(
                context.resources.getString(R.string.message_purchase_one),
                "125 coin"
            )

            Constants.KEY_300_COIN -> String.format(
                context.resources.getString(R.string.message_purchase_one),
                "150 coin"
            )

            else -> String.format(
                context.resources.getString(R.string.message_purchase_one),
                "0 coin"
            )
        }
    }
}


data class BuyCoin(var txtCoin: Int?=null,var price: String?=null)

class SnappingLinearLayoutManager(context: Context?) : LinearLayoutManager(context, HORIZONTAL, false) {
    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
        if (scrolled != 0) {
            val center = width / 2
            val start = center - getChildAt(0)?.width?.div(2)!!
            val end = center + getChildAt(0)?.width?.div(2)!!
            for (i in 0 until childCount) {
                val child = getChildAt(i)!!
                val childCenter = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2
                val d0 = Math.abs(childCenter - center)
                val d1 = Math.abs(childCenter - start)
                val d2 = Math.abs(childCenter - end)
                val scale = 1f - d0.toFloat() / start.toFloat()
                child.scaleX = scale
                child.scaleY = scale
            }
        }
        return scrolled
    }
}
