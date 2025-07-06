package com.example.classnavigator.ui.history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.classnavigator.R
import com.example.classnavigator.data.database.HistoryEntry

/**
 * Адаптер для отображения истории запросов расписания в RecyclerView.
 * @param onClick Клик по карточке (просмотр расписания)
 * @param onDelete Клик по кнопке удаления
 */
class HistoryAdapter(
    private val onClick: (HistoryEntry) -> Unit,
    private val onDelete: (HistoryEntry) -> Unit
) : ListAdapter<HistoryEntry, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view, onClick, onDelete)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(
        itemView: View,
        private val onClick: (HistoryEntry) -> Unit,
        private val onDelete: (HistoryEntry) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val textQuery: TextView = itemView.findViewById(R.id.textQuery)
        private val textDate: TextView = itemView.findViewById(R.id.textDate)
        private val textType: TextView = itemView.findViewById(R.id.textType)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)

        fun bind(entry: HistoryEntry) {
            val queryText = when (entry.searchType) {
                "group" -> "Группа: ${entry.searchValue}"
                "teacher" -> "Преподаватель: ${entry.searchValue}"
                else -> entry.searchValue
            }
            
            textQuery.text = queryText
            textDate.text = entry.date
            textType.text = entry.weekType.replaceFirstChar { it.uppercase() }
            
            itemView.setOnClickListener {
                onClick(entry)
            }
            
            buttonDelete.setOnClickListener {
                onDelete(entry)
            }
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryEntry>() {
        override fun areItemsTheSame(oldItem: HistoryEntry, newItem: HistoryEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryEntry, newItem: HistoryEntry): Boolean {
            return oldItem == newItem
        }
    }
} 