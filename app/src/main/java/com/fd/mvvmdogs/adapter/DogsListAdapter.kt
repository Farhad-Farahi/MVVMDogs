package com.fd.mvvmdogs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.fd.mvvmdogs.R
import com.fd.mvvmdogs.databinding.ItemDogBinding
import com.fd.mvvmdogs.listener.DogClickListener
import com.fd.mvvmdogs.model.DogBreedModel
import com.fd.mvvmdogs.view.fragment.ListFragmentDirections


class DogsListAdapter(val dogsList :ArrayList<DogBreedModel>) :RecyclerView.Adapter<DogsListAdapter.DogViewHolder>(), DogClickListener{


    fun updateDogList(newDogsList : List<DogBreedModel>){
        dogsList.clear()
        dogsList.addAll(newDogsList)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val view = DataBindingUtil.inflate<ItemDogBinding>(inflater,R.layout.item_dog,parent,false)


        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.dog = dogsList[position]
        holder.view.listener = this

    }

    override fun getItemCount(): Int {
        return dogsList.size
    }

    override fun onDogClicked(v: View) {
        val tvDogIdContainer :TextView = v.findViewById<TextView>(R.id.dogId)
        val uuid = tvDogIdContainer.text.toString().toInt()
        val action = ListFragmentDirections.actionListFragmentToDetailFragment(uuid)
        Navigation.findNavController(v).navigate(action)
    }

    class DogViewHolder(var view :ItemDogBinding):RecyclerView.ViewHolder(view.root){
        val tvDogId :TextView = view.dogId
    }
}