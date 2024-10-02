package com.example.socialmedia.ui.minigame


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logoquiz.adapter.HomeAdapter
import com.example.socialmedia.MainActivity
import com.example.socialmedia.model.QuestionLevel
import com.example.socialmedia.R
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.ActivityMainGameBinding
import com.example.socialmedia.ui.minigame.base.BaseActivity
import com.example.socialmedia.data.CreateReadJSON
import com.example.socialmedia.database.AppDatabase
import com.example.socialmedia.ui.minigame.di.Constant
import com.example.socialmedia.ui.minigame.list_question.ListQuestionActivity
import com.example.socialmedia.model.Level
import com.example.socialmedia.model.ProfileSigleton
import com.example.socialmedia.sharePreference.SharePreferences
import com.example.socialmedia.ui.home.HomeViewModel
import com.example.socialmedia.ui.mainfragment.MainFragment
import com.example.socialmedia.ui.minigame.detail_question.DetailQuestionViewModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import org.json.JSONObject
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt

class MainActivityGame : BaseActivity<ActivityMainGameBinding>(), HomeAdapter.OnClickItem {
    private var mListLevel = ArrayList<Level>()
    private val mHomeAdapter = HomeAdapter()
    private val viewModel: HomeViewModel by inject()

    private lateinit var prefs: SharePreferences

    var level: Level? = null

    override fun bindView() {
        viewBinding.icBackGame.click {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()

    }
    override fun observeData() {
        prefs = SharePreferences(applicationContext)
        viewModel.repository.getUid()
        viewModel.getUserData(viewModel.repository.getUid()!!)
        viewModel.userData.observe(this) {
            viewBinding.tvCoin.text = it.gold.toString()
        }
        val receiverLevel = prefs.getString("id_level")
        val receiverNumberQuestionDone = prefs.getInt("count")
        for (i in mListLevel.indices) {
            if (mListLevel[i].level == receiverLevel) {
                mListLevel[i].numberQuestionDone = receiverNumberQuestionDone.toString()
                mListLevel[i].process =
                    (((receiverNumberQuestionDone.toFloat() / 20) * 100 * 100).roundToInt() / 100).toString()
                mHomeAdapter.notifyItemChanged(i)
                AppDatabase.getAppDatabaseInstance(applicationContext).levelDAO()
                    .update(mListLevel[i])

            }
        }
        val obj = JSONObject(CreateReadJSON.getJSONDataFromAsset(this, "Level.json")!!)
        val jsonArr = obj.getJSONArray("level")
        for (i in 0 until jsonArr.length()) {
            val jsonObs: JSONObject? = jsonArr.getJSONObject(i)
            val id: Int = jsonObs!!.getInt("id")
            val image: String = jsonObs.getString("image")
            val level1: String = jsonObs.getString("level")
            val numberQuestion: String = jsonObs.getString("numberQuestion")
            val numberQuestionDone: String = jsonObs.getString("numberQuestionDone")
            val process: String = jsonObs.getString("process")
            val listQuestionLevel = jsonObs.getJSONArray("listQuestionLevel")
            val list: ArrayList<QuestionLevel> =
                Gson().fromJson(
                    listQuestionLevel.toString(),
                    object : TypeToken<ArrayList<QuestionLevel?>?>() {}.type
                )
            val level = Level(id, image, level1, numberQuestion, numberQuestionDone, process, list)
            AppDatabase.getAppDatabaseInstance(applicationContext).levelDAO().insertLevel(level)
            mListLevel = (AppDatabase.getAppDatabaseInstance(applicationContext).levelDAO()
                .getAllLevel()) as ArrayList<Level>
            mHomeAdapter.submitList(mListLevel)
            viewBinding.rcvHome.adapter = mHomeAdapter
            viewBinding.rcvHome.layoutManager =
                GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
            mHomeAdapter.listener = this
        }
    }


    override fun layoutId(): Int = R.layout.activity_main_game
    override fun onClickLevel(position: Int, level: Level) {
        val item = Gson().toJson(mListLevel[position])
        val bundle = Bundle()
        bundle.putSerializable(
            Constant.LEVEL,
            level
        )
        startActivityForResult(Intent(this, ListQuestionActivity::class.java).apply {
            putExtra(Constant.DETAIL_LEVEL, item)
            putExtras(bundle)
        }, Constant.QUESTION_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.QUESTION_REQUEST && resultCode == RESULT_OK) {
            val receiverLevel = data?.extras?.getString(Constant.ID_LEVEL)
            val receiverNumberQuestionDone = data?.extras?.getInt(Constant.QUESTION_DONE_NUMBERS)
            for (i in mListLevel.indices) {
                if (mListLevel[i].level == receiverLevel) {
                    mListLevel[i].numberQuestionDone = receiverNumberQuestionDone.toString()
                    if (receiverNumberQuestionDone != null) {
                        mListLevel[i].process =
                            (((receiverNumberQuestionDone.toFloat() / 20) * 100 * 100).roundToInt() / 100).toString()
                    }
                    mHomeAdapter.notifyItemChanged(i)
                    AppDatabase.getAppDatabaseInstance(applicationContext).levelDAO()
                        .update(mListLevel[i])

                }
            }
        }
    }

}