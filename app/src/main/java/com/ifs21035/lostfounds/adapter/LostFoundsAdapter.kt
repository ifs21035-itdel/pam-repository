package com.ifs21035.lostfounds.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ifs21035.lostfounds.data.remote.response.LostFoundsItemResponse
import com.ifs21035.lostfounds.databinding.ItemRowLostfoundBinding

class LostFoundsAdapter :
    ListAdapter<LostFoundsItemResponse,
            LostFoundsAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private var originalData = mutableListOf<LostFoundsItemResponse>()
    private var filteredData = mutableListOf<LostFoundsItemResponse>()
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowLostfoundBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = originalData[originalData.indexOf(getItem(position))]
        holder.binding.cbItemLostFound.setOnCheckedChangeListener(null)
        holder.binding.cbItemLostFound.setOnLongClickListener(null)
        holder.bind(data)
        holder.binding.cbItemLostFound.setOnCheckedChangeListener { _, isChecked ->
            data.isCompleted = if (isChecked) 1 else 0
            holder.bind(data)
            onItemClickCallback.onCheckedChangeListener(data, isChecked)
        }
        holder.binding.ivItemLostFoundDetail.setOnClickListener {
            onItemClickCallback.onClickDetailListener(data.id)
        }
    }
    class MyViewHolder(val binding: ItemRowLostfoundBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LostFoundsItemResponse) {
            binding.apply {
                tvItemLostFoundTitle.text = data.title
                cbItemLostFound.isChecked = data.isCompleted == 1
            }
        }
    }
    fun submitOriginalList(list: List<LostFoundsItemResponse>) {
        originalData = list.toMutableList()
        filteredData = list.toMutableList()
        submitList(originalData)
    }
    fun filter(query: String) {
        filteredData = if (query.isEmpty()) {
            originalData
        } else {
            originalData.filter {
                (it.title.contains(query, ignoreCase = true))
            }.toMutableList()
        }
        submitList(filteredData)
    }
    interface OnItemClickCallback {
        fun onCheckedChangeListener(lostFound: LostFoundsItemResponse, isChecked: Boolean)
        fun onClickDetailListener(lostFoundId: Int)
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LostFoundsItemResponse>() {
            override fun areItemsTheSame(
                oldItem: LostFoundsItemResponse,
                newItem: LostFoundsItemResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(
                oldItem: LostFoundsItemResponse,
                newItem: LostFoundsItemResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
