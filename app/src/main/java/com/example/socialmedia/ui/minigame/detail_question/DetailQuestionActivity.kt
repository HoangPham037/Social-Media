package com.example.socialmedia.ui.minigame.detail_question

import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.logoquiz.adapter.GridViewAnswerAdapter
import com.example.logoquiz.adapter.GridViewSuggestAdapter
import com.example.socialmedia.common.State
import com.example.socialmedia.model.QuestionLevel
import com.example.socialmedia.ui.minigame.base.BaseActivity
import com.example.socialmedia.database.AppDatabase
import com.example.socialmedia.ui.minigame.di.Constant
import com.example.socialmedia.ui.minigame.di.SpacingItemDecoration
import com.example.socialmedia.model.AnswerModel
import com.example.socialmedia.model.SuggestModel
import com.example.socialmedia.sharePreference.SharePreferences
import com.example.socialmedia.databinding.ActivityDetailQuestionBinding
import com.example.socialmedia.ui.minigame.BuyCoinActivity
import com.example.socialmedia.ui.minigame.dialog.ConfirmDialogFragment
import com.example.socialmedia.ui.minigame.dialog.FailedDialogFragment
import com.example.socialmedia.ui.minigame.dialog.ReleaseDialogFragment
import com.example.socialmedia.ui.minigame.dialog.SuccessDialogFragment
import com.example.socialmedia.ui.minigame.dialog.UnLockDialogFragment
import org.koin.android.ext.android.inject
import kotlin.collections.ArrayList


class DetailQuestionActivity : BaseActivity<ActivityDetailQuestionBinding>(),
    GridViewSuggestAdapter.OnClickItemSuggest, SuccessDialogFragment.OnClickButton,
    ConfirmDialogFragment.OnClickConfirm, FailedDialogFragment.OnClickFailed,
    UnLockDialogFragment.OnClickUnlock, ReleaseDialogFragment.OnClickRelease {
    var questionLevel: QuestionLevel? = null
    var count = 0
    var coin = 0
    var result = ArrayList<String>()

    private val viewModel : DetailQuestionViewModel by inject()
    private val suggest = ArrayList<SuggestModel>()
    private var answerList = ArrayList<AnswerModel>()
    private val suggestAdapter = GridViewSuggestAdapter()
    private val answerAdapter = GridViewAnswerAdapter()

    private val resultList = ArrayList<String>()

    private val confirmDialog = ConfirmDialogFragment()

    private val failedDialog = FailedDialogFragment()

    private val unlockDialog = UnLockDialogFragment()

    lateinit var answer: CharArray

    lateinit var  pref : SharePreferences

    private var listQuestion = ArrayList<QuestionLevel>()

    private var index: Int? = null

    private var listItemRelease = ArrayList<SuggestModel>()


    override fun layoutId(): Int = R.layout.activity_detail_question

    override fun bindView() {
        pref = SharePreferences(applicationContext)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        suggestAdapter.listener = this
        confirmDialog.listenerConfirm = this
        failedDialog.listenerAgain = this
        unlockDialog.listenerUnLock = this

        viewBinding.icBackHome.setOnClickListener { onBackPressed() }
        viewBinding.plusCoin.setOnClickListener {
            startActivity(Intent(this,BuyCoinActivity::class.java))
        }

        //unlock
        viewBinding.btnUnlock.setOnClickListener {
            unlockDialog.show(supportFragmentManager, "")
            unlockDialog.isCancelable = false
        }
        //suggest
        viewBinding.btnSuggest.setOnClickListener {
            confirmDialog.show(supportFragmentManager, "")
            confirmDialog.isCancelable = false
        }
        //delete all character
        viewBinding.btnDelete.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(Constant.QUESTION_LEVEL, listQuestion[index!!]) //cái này à
            bundle.putInt(Constant.POSITION, index!!)
            startActivity(Intent(this, DetailQuestionActivity::class.java).apply {
                putExtras(bundle)
                putExtra("listQuestion", listQuestion)

            })
            //clear list character old
            result.clear()
            val mp = MediaPlayer.create(this, R.raw.letter_removed)
            mp.start()
            mp.setOnCompletionListener { sound ->
                sound.release()
            }
            finish()
        }


    }

    override fun observeData() {
        //đây là đoạn nhận model + position + list question từ màn kia
        questionLevel = intent?.extras?.getSerializable(Constant.QUESTION_LEVEL) as? QuestionLevel?
        index = intent?.extras?.getInt(Constant.POSITION)
        listQuestion = intent?.getSerializableExtra("listQuestion") as ArrayList<QuestionLevel>

        viewModel.repository.getProfile(viewModel.repository.getUid()){
            when (it){
                is State.Success -> {
                    coin = it.data.gold
                    viewBinding.tvCoin.text = coin.toString()
                    pref.save(Constant.KEY_COIN,coin)

                }

                else -> {
                    pref.save(Constant.KEY_COIN,coin)
                }
            }
        }
        //get model questionLevel
        if (listQuestion[index!!].imageQuestion != null) {
            Glide.with(this).load("file:///android_asset/image/${listQuestion[index!!].imageQuestion}")
                .into(
                    viewBinding.imgQuestionDetail
                )
        }
        if (listQuestion[index!!].question != null) viewBinding.questionNumber.text =
            listQuestion[index!!].question.toString()
        answer = listQuestion[index!!].name.replace(" ", "")
            .substring(listQuestion[index!!].name.lastIndexOf("/") + 1).toCharArray()
        answer.forEach {
            resultList.add(it.toString())
        }
        //list character quiz
        viewModel.randomList(answer)
        viewModel.suggests.observe(this) { list ->
            suggest.addAll(list)
            listItemRelease.addAll(list)
            suggestAdapter.submitList(suggest)
            viewBinding.rcvQuestion.adapter = suggestAdapter
            viewBinding.rcvQuestion.addItemDecoration(SpacingItemDecoration(20))
        }

        //list answer null
        answerAdapter.submitList(setUpNullList())
        viewBinding.rcvAnswer.adapter = answerAdapter
        viewBinding.rcvAnswer.addItemDecoration(SpacingItemDecoration(20))


    }


    private fun setUpNullList(): MutableList<AnswerModel> {
        for (i in answer.indices) {
            answerList.add(AnswerModel(" ", false))
        }
        return answerList
    }

    override fun onClickSuggest(position: Int, suggest: SuggestModel) {
        if (count < answer.size) {
            val mp = MediaPlayer.create(this, R.raw.click)
            mp.start()
            mp.setOnCompletionListener { sound ->
                sound.release()
            }
           suggestAdapter.currentList[position].apply {
               isCheck = true
               if (labelSuggest != "") result.add(labelSuggest)
               try {
                 if (labelSuggest != "") {
                     Log.d("mm", result[count].toString())
                     Log.d("result", result.toString())
                     answerList[count] = AnswerModel(result[count], false)
                     answerAdapter.notifyItemChanged(count)
                     count++

                 }
                   labelSuggest = ""
               } catch (e: Exception) {
                   print(e.printStackTrace())
               }
           }

            suggestAdapter.notifyItemChanged(position)

            if (count == resultList.size) {
                if (answerList[count - 1].label != " ") {
                    if (result == resultList) {
                        if (questionLevel!!.isDone) {
                            val releaseDialog =
                                ReleaseDialogFragment.newInstance(
                                    questionLevel!!.name,
                                    questionLevel!!.question
                                )
                            releaseDialog.listenerRelease = this
                            releaseDialog.show(supportFragmentManager, "")
                            releaseDialog.isCancelable = false
                            val mp = MediaPlayer.create(this, R.raw.right_answer)
                            mp.start()
                            mp.setOnCompletionListener { sound ->
                                sound.release()
                            }
                            listQuestion[index!!].isDone =
                                true
                            pref.save("id", listQuestion[index!!].question)
                            pref.save("check", listQuestion[index!!].isDone)
                            pref.save("uid", listQuestion[index!!].uid)
                            val intent = Intent()
                            intent.putExtra("id", listQuestion[index!!].question)
                            intent.putExtra("check",  listQuestion[index!!].isDone)
                            intent.putExtra("uid", listQuestion[index!!].uid)
                            setResult(123, intent)
                        } else {
                            val successDialog =
                                SuccessDialogFragment.newInstance(
                                    questionLevel!!.name,
                                    questionLevel!!.question,
                                    10
                                )
                            successDialog.listener = this
                            successDialog.show(supportFragmentManager, "")
                            successDialog.isCancelable = false
                            val mp = MediaPlayer.create(this, R.raw.right_answer)
                            mp.start()
                            mp.setOnCompletionListener { sound ->
                                sound.release()
                            }
                            listQuestion[index!!].isDone =
                                true
                            coin += 10
                            viewModel.repository.updateGold(coin){
                                when(it){
                                    is State.Success -> {
                                        viewBinding.tvCoin.text = coin.toString()

                                    }

                                    else -> {
                                        viewBinding.tvCoin.text = "0"
                                    }
                                }
                            }
                            AppDatabase.getAppDatabaseInstance(applicationContext)
                                .questionLevelDAO()
                                .update(listQuestion[index!!].isDone, listQuestion[index!!].question, listQuestion[index!!].uid)
                            val intent = Intent()
                            intent.putExtra("id", listQuestion[index!!].question)
                            intent.putExtra("check",  listQuestion[index!!].isDone)
                            intent.putExtra("uid", listQuestion[index!!].uid)
                            setResult(123, intent)
                        }

                    } else {
                        failedDialog.show(supportFragmentManager, "")
                        failedDialog.isCancelable = false
                        val mp = MediaPlayer.create(this, R.raw.wrong_answer)
                        mp.start()
                        mp.setOnCompletionListener { sound ->
                            sound.release()
                        }
                    }
                }
            }
        }
    }


    //back home
    override fun onClickHome() {
        val intent = Intent()
        listQuestion[index!!].isDone =
            true
        pref.save("id", listQuestion[index!!].question)
        pref.save("check", listQuestion[index!!].isDone)
        pref.save("uid", listQuestion[index!!].uid)
        AppDatabase.getAppDatabaseInstance(applicationContext)
            .questionLevelDAO()
            .update(listQuestion[index!!].isDone, listQuestion[index!!].question, listQuestion[index!!].uid)
        finish()
    }

    //next question
    override fun onClickNext() {
        AppDatabase.getAppDatabaseInstance(applicationContext)
            .questionLevelDAO()
            .update(listQuestion[index!!].isDone, listQuestion[index!!].question, listQuestion[index!!].uid)
        val successDialog =
            SuccessDialogFragment.newInstance(questionLevel!!.name, questionLevel!!.question, 50)
        successDialog.listener = this
        val int = index!!?.plus(1)
        val bundle = Bundle()
        bundle.putSerializable(Constant.QUESTION_LEVEL, listQuestion[int!!]) //cái này à
        bundle.putInt(Constant.POSITION, int)
        startActivity(Intent(this, DetailQuestionActivity::class.java).apply {
            putExtras(bundle)
            putExtra("listQuestion", listQuestion)
        })

        finish()

    }

    //not confirm
    override fun onClickNo() {
        confirmDialog.dismiss()
    }

    //confirm suggest
    override fun onClickYes() {
        if (coin >= 10) {
            for ((index, value) in suggest.withIndex()) {
                if (answer.none { it.toString() == value.labelSuggest }) {
                    suggest[index] = SuggestModel(" ", true)
                    suggestAdapter.notifyItemChanged(index)
                }
            }
            confirmDialog.dismiss()
            val mp = MediaPlayer.create(this, R.raw.explosion_sound)
            mp.start()
            mp.setOnCompletionListener { sound ->
                sound.release()
            }
            coin -= 10
            viewModel.repository.updateGold(coin){
                when(it){
                    is State.Success -> {
                        viewBinding.tvCoin.text = coin.toString()
                    }

                    else -> {

                    }
                }
            }

        } else {
            Toast.makeText(
                this,
                "Bạn không đủ vàng để sử dụng tính năng này!",
                Toast.LENGTH_SHORT
            ).show()
        }



    }

    //try again
    override fun onClick() {
        val bundle = Bundle()
        bundle.putSerializable(Constant.QUESTION_LEVEL, listQuestion[index!!]) //cái này à
        bundle.putInt(Constant.POSITION, index!!)
        startActivity(Intent(this, DetailQuestionActivity::class.java).apply {
            putExtras(bundle)
            putExtra("listQuestion", listQuestion)

        })
       finish()
        result.clear()
        failedDialog.dismiss()
    }

    //no confirm unlock
    override fun onClickNoUnlock() {
        unlockDialog.dismiss()
    }

    //confirm unlock
    override fun onClickYesUnlock() {

        if (coin >= 10) {
            coin -= 10
            viewModel.repository.updateGold(coin){
                when(it){
                    is State.Success -> {
                        viewBinding.tvCoin.text = coin.toString()
                        pref.save(Constant.KEY_COIN,coin)
                    }

                    else -> {
                        pref.save(Constant.KEY_COIN,coin)
                    }
                }
            }

            answer.forEachIndexed { index, c ->
                answerList[index] = AnswerModel(c.toString(), false)
                answerAdapter.notifyItemChanged(index)
            }
            for ((index, value) in suggest.withIndex()) {
                if (answer.any { it.toString() == value.labelSuggest }) {
                    suggest[index] = SuggestModel(" ", true)
                    suggestAdapter.notifyItemChanged(index)
                }
            }
            unlockDialog.dismiss()
            val mp = MediaPlayer.create(this, R.raw.cartoon_success_fanfair)
            mp.start()
            mp.setOnCompletionListener { sound ->
                sound.release()
            }

            listQuestion[index!!].isDone = true
            val successDialogFragment =
                SuccessDialogFragment.newInstance(questionLevel!!.name, questionLevel!!.question, 0)
            successDialogFragment.listener = this
            successDialogFragment.show(supportFragmentManager, "")
            AppDatabase.getAppDatabaseInstance(applicationContext)
                .questionLevelDAO()
                .update(listQuestion[index!!].isDone, listQuestion[index!!].question, listQuestion[index!!].uid)
            //coin = pref.getInt(Constant.KEY_COIN)
            //get model questionLevel
            viewBinding.tvCoin.text = coin.toString()
        } else {
            Toast.makeText(
                this,
                "Bạn không đủ vàng để thực hiện tính năng này!",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    override fun onClickRelease() {
        finish()
    }

    override fun onClickNextRelease() {
        val releaseDialog =
            ReleaseDialogFragment.newInstance(questionLevel!!.name, questionLevel!!.question)
        releaseDialog.listenerRelease = this
        val int = index!!.plus(1)
        val bundle = Bundle()
        bundle.putSerializable(Constant.QUESTION_LEVEL, listQuestion[int!!])
        bundle.putInt(Constant.POSITION, int)
        startActivity(Intent(this, DetailQuestionActivity::class.java).apply {
            putExtras(bundle)
            putExtra("listQuestion", listQuestion)
        })
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppDatabase.getAppDatabaseInstance(applicationContext)
            .questionLevelDAO()
            .update(listQuestion[index!!].isDone, listQuestion[index!!].question, listQuestion[index!!].uid)
        pref.save("id", listQuestion[index!!].question)
        pref.save("check", listQuestion[index!!].isDone)
        pref.save("uid", listQuestion[index!!].uid)
        finish()
    }





}