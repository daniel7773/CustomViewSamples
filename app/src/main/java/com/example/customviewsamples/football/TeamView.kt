package com.example.customviewsamples.football

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import com.example.customviewsamples.databinding.TeamViewBinding

class TeamView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val binding = TeamViewBinding
        .inflate(LayoutInflater.from(context), this, true)
    private val memberViews: MutableList<View> = mutableListOf()

    init {
        binding.teamWrapper.background = HalfFieldDrawable(context)
        radius = 8.toFloat()
    }

    fun bind(teamColor: Int, members: List<Int>) {


    }
}