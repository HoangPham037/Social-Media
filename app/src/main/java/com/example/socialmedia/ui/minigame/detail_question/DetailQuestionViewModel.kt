package com.example.socialmedia.ui.minigame.detail_question

import androidx.lifecycle.MutableLiveData
import com.example.socialmedia.base.BaseViewModel
import com.example.socialmedia.model.SuggestModel
import com.example.socialmedia.repository.Repository
import com.example.socialmedia.ui.minigame.di.Common
import java.util.Random

class DetailQuestionViewModel(val repository: Repository)  : BaseViewModel(repository) {

    private var suggestSource = ArrayList<String>()

    var suggests = MutableLiveData<ArrayList<SuggestModel>>()

    private val suggest = ArrayList<SuggestModel>()

    fun randomList(answer : CharArray){
        val random = Random()
        suggestSource.clear()
        for(i in 0..2) suggestSource.add(Common.alphabet_character[random.nextInt(Common.alphabet_character.size)])
        for (i in answer) suggestSource.add(i.toString())
        suggestSource.shuffle()
        suggestSource.forEach {
            suggest.add(SuggestModel(it, false))
            suggests.value = suggest
        }
    }


}