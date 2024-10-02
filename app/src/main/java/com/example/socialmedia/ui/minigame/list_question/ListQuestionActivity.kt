package com.example.socialmedia.ui.minigame.list_question

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.example.socialmedia.R
import com.example.logoquiz.adapter.ListQuestionAdapter
import com.example.socialmedia.model.QuestionLevel
import com.example.socialmedia.ui.minigame.base.BaseActivity
import com.example.socialmedia.database.AppDatabase
import com.example.socialmedia.ui.minigame.detail_question.DetailQuestionActivity
import com.example.socialmedia.ui.minigame.di.Constant
import com.example.socialmedia.ui.minigame.dialog.CompleteDialogFragment
import com.example.socialmedia.model.Level
import com.example.socialmedia.sharePreference.SharePreferences
import com.example.socialmedia.databinding.ActivityListQuestionBinding
import com.example.socialmedia.ui.minigame.detail_question.DetailQuestionViewModel
import com.google.gson.Gson
import org.koin.android.ext.android.inject

class ListQuestionActivity : BaseActivity<ActivityListQuestionBinding>(),
    ListQuestionAdapter.OnCLickItem{
    private var level: Level? = null
    private val mListQuestionAdapter = ListQuestionAdapter()
    private var count = 0


    private var listQuestionLevel = ArrayList<QuestionLevel>()

    private val viewModel : DetailQuestionViewModel by inject()
    private lateinit var pref : SharePreferences
    override fun layoutId(): Int = R.layout.activity_list_question

    override fun bindView() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        viewBinding.icBack.setOnClickListener { onBackPressed() }
        mListQuestionAdapter.listener = this

    }

    override fun observeData() {
        pref = SharePreferences(applicationContext)
        val valueObs = intent.getStringExtra(Constant.DETAIL_LEVEL)
        level = intent.extras?.getSerializable(Constant.LEVEL) as? Level?
        if (level?.level != null) {
            viewBinding.titleNumberLevel.text = level?.level.toString()
        }
        val arrListQuestion = Gson().fromJson(valueObs, Level::class.java)

       AppDatabase.getAppDatabaseInstance(applicationContext).questionLevelDAO().onInsertOrUpdate(arrListQuestion.listQuestionLevel)
        Log.d("all", AppDatabase.getAppDatabaseInstance(applicationContext).questionLevelDAO().getAllQuestionLevel().toString())
        Log.d("size", AppDatabase.getAppDatabaseInstance(applicationContext).questionLevelDAO().getAllQuestionLevel().size.toString() )
        listQuestionLevel =
            AppDatabase.getAppDatabaseInstance(applicationContext).questionLevelDAO()
                .getAllQuestionLevel(level!!.level) as ArrayList<QuestionLevel>
        mListQuestionAdapter.submitList(
           listQuestionLevel
        )
        viewBinding.rcvListQuestion.adapter = mListQuestionAdapter
        for (i in listQuestionLevel.indices){
            if (listQuestionLevel[i].isDone){
                if(count < 20){
                    count++
                    Log.d("numbers", count.toString())
                }
            }
        }
       // AppDatabase.getAppDatabaseInstance(applicationContext).levelDAO().update(co)
        pref.save("id_level", level!!.level)
        pref.save("count", count)
        val intent = Intent()
        val sendData = Bundle()
        sendData.putString(Constant.ID_LEVEL, level!!.level)
        sendData.putInt(Constant.QUESTION_DONE_NUMBERS, count)
        sendData.putInt(Constant.ID, level!!.id)
        intent.putExtras(sendData)
        setResult(RESULT_OK, intent)
        //check index
        if (listQuestionLevel.all { it.isDone }){
            val completeDialog = CompleteDialogFragment.getInstanceComplete(level!!.id)
            completeDialog.show(supportFragmentManager, "")
        }


    }

    override fun onClick(questionLevel: QuestionLevel, position : Int) {
        val bundle = Bundle()
        bundle.putSerializable(Constant.QUESTION_LEVEL, questionLevel)
        bundle.putInt(Constant.POSITION, position)
        startActivityForResult(Intent(this, DetailQuestionActivity::class.java).apply {
            putExtras(bundle)
            putExtra("listQuestion", listQuestionLevel)
        }, Constant.QUESTION_REQUEST)
        val mp = MediaPlayer.create(this, R.raw.btn_click_sound)
        mp.start()
        mp.setOnCompletionListener { sound ->
            sound.release()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.QUESTION_REQUEST && resultCode == 123) {
            val receiverId = data?.extras?.getInt("id")
            val receiverCheckDone = data?.extras?.getBoolean("check")
            val receiverLevelDetail = data?.extras?.getInt("uid")
            val question = pref.getInt("question")
            val isDone = pref.getBoolean("isDone")
            val uid = pref.getInt("uid")
            for (i in listQuestionLevel.indices) {
                if (listQuestionLevel[i].question == receiverId && listQuestionLevel[i].uid == receiverLevelDetail) {
                    listQuestionLevel[i].isDone = receiverCheckDone!!
                    mListQuestionAdapter.notifyItemChanged(i)
                    AppDatabase.getAppDatabaseInstance(applicationContext).questionLevelDAO()
                        .update(receiverCheckDone, receiverId, receiverLevelDetail)
                    Log.d(
                        "k1",
                        receiverLevelDetail.toString()
                    )
                    Log.d("id", receiverId.toString())
                    Log.d("done", receiverCheckDone.toString())
                }

            }

            count = 0
            for (i in listQuestionLevel.indices) {
                if (listQuestionLevel[i].isDone) {
                    if (count < 20) {
                        count++
                        Log.d("numbers", count.toString())
                    }
                }
            }
        }
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent()
        val sendData = Bundle()
        sendData.putString(Constant.ID_LEVEL, level!!.level)
        sendData.putInt(Constant.QUESTION_DONE_NUMBERS, count)
        sendData.putInt(Constant.ID, level!!.id)
        intent.putExtras(sendData)
        setResult(RESULT_OK, intent)
        finish()
    }
    override fun onResume() {
        super.onResume()
        listQuestionLevel =
            AppDatabase.getAppDatabaseInstance(applicationContext).questionLevelDAO()
                .getAllQuestionLevel(level!!.level) as ArrayList<QuestionLevel>
        mListQuestionAdapter.submitList(
            listQuestionLevel
        )
        mListQuestionAdapter.notifyDataSetChanged()
        count = 0
        for (i in listQuestionLevel.indices) {
            if (listQuestionLevel[i].isDone) {
                if (count < 20) {
                    count++
                    Log.d("numbers", count.toString())
                }
            }
        }
        //check index
        if (listQuestionLevel.all { it.isDone }){
            val completeDialog = CompleteDialogFragment.getInstanceComplete(level!!.id)
            completeDialog.show(supportFragmentManager, "")
        }
    }

}


