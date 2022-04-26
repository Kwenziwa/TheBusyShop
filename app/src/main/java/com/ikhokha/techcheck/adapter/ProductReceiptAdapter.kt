package com.ikhokha.techcheck.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.ikhokha.techcheck.R
import com.ikhokha.techcheck.model.Product

class ProductReceiptAdapter(val context: Context, private val products: List<Product>): RecyclerView.Adapter<ProductReceiptAdapter.ProductReceiptViewHolder>() {

    class ProductReceiptViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val productDescription: AppCompatTextView = itemView.findViewById(R.id.receipt_item)
        val productPrice: AppCompatTextView = itemView.findViewById(R.id.receipt_price)
        val productQuantity: AppCompatTextView = itemView.findViewById(R.id.receipt_quantity)
        val productAmount: AppCompatTextView = itemView.findViewById(R.id.receipt_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductReceiptViewHolder {
        return ProductReceiptViewHolder(LayoutInflater.from(context).inflate(R.layout.receipt_item,parent,false))
    }

    override fun onBindViewHolder(holder: ProductReceiptViewHolder, position: Int) {
        holder.productDescription.text = products[position].description
        holder.productPrice.text = context.getString(R.string.price_text,products[position].price)
        holder.productQuantity.text = products[position].quantity.toString()
        holder.productAmount.text = context.getString(R.string.price_text,(products[position].quantity!!*products[position].price!!))
            .replace(",",".")
    }

    override fun getItemCount(): Int {
        return products.size
    }
}