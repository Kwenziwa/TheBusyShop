package com.ikhokha.techcheck.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ikhokha.techcheck.R
import com.ikhokha.techcheck.model.Product


class ProductCartAdapter(val context: Context,private val productList: List<Product>,val onClickAction: (clickedProduct: Product)->Unit): RecyclerView.Adapter<ProductCartAdapter.ProductCartViewHolder>() {

    class ProductCartViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val productDescription: AppCompatTextView = itemView.findViewById(R.id.product_description)
        val productPrice: AppCompatTextView = itemView.findViewById(R.id.product_price)
        val productQuantity: AppCompatTextView = itemView.findViewById(R.id.product_quantity)
        val productImg: AppCompatImageView = itemView.findViewById(R.id.product_img)
        val productCard: CardView = itemView.findViewById(R.id.product_cardview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCartViewHolder {
        return ProductCartViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_product_view,parent,false))
    }

    override fun onBindViewHolder(holder: ProductCartViewHolder, position: Int) {
        holder.productDescription.text = productList[position].description
        holder.productPrice.text = context.getString(R.string.price_text,productList[position].price).replace(",",".")
        holder.productQuantity.text = context.getString(R.string.quantity_text,productList[position].quantity)

       Glide.with(context)
           .asBitmap()
           .load(productList[position].imageUrl)
           .diskCacheStrategy(DiskCacheStrategy.ALL)
           .placeholder(R.drawable.error_loader)
           .error(R.drawable.error_loader)
           .into(holder.productImg)


        holder.productCard.setOnClickListener {
            onClickAction(productList[position])
        }

    }

    override fun getItemCount(): Int {
        return productList.size
    }
}