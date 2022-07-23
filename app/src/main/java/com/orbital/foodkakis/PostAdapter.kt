package com.orbital.foodkakis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_profile.*
import java.time.format.TextStyle

class PostAdapter(private val postList: ArrayList<Post>): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostAdapter.PostViewHolder, position: Int) {
        val post : Post = postList[position]
//        holder.image.
//        holder.image.setImageURI(post.image!!.toUri())
//        Glide.
        Glide.with(holder.itemView.context)
            .load(post.image)
            .centerCrop()
            .into(holder.image)
        holder.header.text = post.header
        holder.validity.text = post.validity
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    public class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image : ImageView = itemView.findViewById(R.id.tvImage)
        val header : TextView = itemView.findViewById(R.id.tvHeader)
        val validity : TextView = itemView.findViewById(R.id.tvValidity)
    }
}