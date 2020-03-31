package com.example.camerasample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.realm.RealmResults
import java.util.zip.Inflater

class MyRecyclerViewAdapter(val results: RealmResults<PictureModel>):RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>() {

    var myListener: MyRecyclerViewInterface? = null

    class MyViewHolder(val view: View):RecyclerView.ViewHolder(view){
        var photo: ImageView;

        init {
            photo = view.findViewById(R.id.unitImageView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_unit, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pictureUri = results[position]?.pictureUri

        Glide.with(MyApplication.appContext).load(pictureUri).into(holder.photo)

        holder.view.setOnClickListener {
            myListener?.onClick(pictureUri.toString())
        }

    }

    override fun getItemCount(): Int {
        return results.size
    }

    interface MyRecyclerViewInterface{

        fun onClick(strPictureUri:String)
    }


}