package com.example.socialmedia.ui.chat.chatscreen.chatwithuser

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.base.utils.gone
import com.example.socialmedia.base.utils.showToast
import com.example.socialmedia.base.utils.visible
import com.example.socialmedia.common.State
import com.example.socialmedia.databinding.FragmentChatWithUserBinding
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.model.MessageModel
import com.example.socialmedia.model.MuteNotification
import com.example.socialmedia.model.Turnofnotification
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.viewprofile.ViewProfileFragment
import com.example.socialmedia.ui.chat.chatscreen.chatwithuser.viewprofile.ViewProfileViewModel
import com.example.socialmedia.ui.utils.Constants
import com.example.socialmedia.ui.utils.DateProvider
import com.example.socialmedia.ui.utils.ImageUtils
import com.example.socialmedia.ui.utils.formatStringBlock
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vanniktech.emoji.EmojiPopup
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject
import java.io.File
import java.util.Date
import java.util.UUID


class ChatWithUserFragment : BaseFragmentWithBinding<FragmentChatWithUserBinding>(),
    Timer.OnTimeClickListener {

    private val mainViewModel: MainViewModel by inject()
    private val chatWithUserViewModel: ChatWithUserViewModel by inject()
    private val viewProfileViewModel: ViewProfileViewModel by inject()
    private var receiverUid: String? = ""
    private lateinit var conversionId: String
    private lateinit var amplitudes: ArrayList<Float>
    private var listChat = mutableListOf<MessageModel>()
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false
    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var fileName = ""
    private var timeRecording = ""
    private lateinit var timer: Timer
    private lateinit var singleChatWithUserAdapter: ChatWithUserAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    val sharedPreferences: Preferences by inject()

    override fun getViewBinding(inflater: LayoutInflater): FragmentChatWithUserBinding {
        return FragmentChatWithUserBinding.inflate(layoutInflater)
    }

    override fun init() {
        permissionGranted = ActivityCompat.checkSelfPermission(
            requireActivity(),
            permissions[0]
        ) == PackageManager.PERMISSION_GRANTED
        timer = Timer(this)
    }

    override fun initData() {
        receiverUid = arguments?.getString(Constants.KEY_USER_ID) ?: ""
        val uid = mainViewModel.getUid() ?: ""
        conversionId =
            chatWithUserViewModel.getConversionId(uid, receiverUid!!)
        val lastConversionSenderId = arguments?.getString(Constants.KEY_LAST_MESS_SENDER_ID) ?: ""
        if (uid != lastConversionSenderId) {
            chatWithUserViewModel.updateStateSeen(true, conversionId)
        }
        chatWithUserViewModel.otherUserInfo.observe(viewLifecycleOwner) { userReceiver ->
            val isBlock = userReceiver.listBlock?.any {
                it == mainViewModel.getUid()
            }
            chatWithUserViewModel.userInfo.observe(viewLifecycleOwner) { userSend ->
                /**observer send Uid*/
                val isBlockOtherUser = userSend.listBlock?.any {
                    it == receiverUid
                }
                if (isBlockOtherUser == true) {
                    binding.layoutBlockOtherUser.visible()
                    binding.tvBlockName.text = userReceiver.name?.formatStringBlock()
                    binding.tvDesBlocks.gone()
                    binding.layoutChats.gone()
                } else if (isBlockOtherUser == false && isBlock == true) {
                    binding.tvDesBlocks.visible()
                    binding.layoutBlockOtherUser.gone()
                    binding.layoutChats.gone()

                } else {
                    binding.layoutBlockOtherUser.gone()
                    binding.tvDesBlocks.gone()
                    binding.layoutChats.visible()
                }
            }
            chatWithUserViewModel.getUserByUserId(Constants.KEY_COLLECTION_USER)

            binding.tvName.text = userReceiver.name
            binding.tvType.text = userReceiver.status

            if (userReceiver.avtPath != null) {
                Glide.with(requireActivity()).load(userReceiver.avtPath)
                    .placeholder(R.drawable.avatar).fallback(R.drawable.avatar)
                    .into(binding.imgAvatar)
            }

            if (userReceiver.status == Constants.KEY_STATE_ONLINE) {
                binding.imgActiveState.visible()
            } else {
                binding.imgActiveState.gone()
            }
        }
        chatWithUserViewModel.getOtherUserById(receiverUid!!, Constants.KEY_COLLECTION_USER)
        setupChatRecyclerview()
    }

    private fun addOrUpdateConversion(lastMessage: String) {
        chatWithUserViewModel.addOrUpdateConversionReference(
            conversionId,
            mainViewModel.getUid()!!,
            receiverUid!!,
            lastMessage,
            stateSeen = false
        )
    }

    private fun setupChatRecyclerview() {
        chatWithUserViewModel.listMessage.observe(viewLifecycleOwner) { listChatMessage ->
            if (listChatMessage.isNotEmpty()) {
                singleChatWithUserAdapter =
                    ChatWithUserAdapter(mainViewModel.getUid()!!, requireContext())
                linearLayoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.rcChat.layoutManager = linearLayoutManager
                binding.rcChat.setHasFixedSize(true)
                binding.rcChat.adapter = singleChatWithUserAdapter
                listChat = listChatMessage as MutableList<MessageModel>
                listChat.sortWith(compareBy(MessageModel::timestamp))
                singleChatWithUserAdapter.submitList(listChat)
                if (listChat.isEmpty()) {
                    singleChatWithUserAdapter.notifyDataSetChanged()
                } else {
                    singleChatWithUserAdapter.notifyItemRangeInserted(listChat.size, listChat.size)
//                    binding.rcChat.smoothScrollToPosition(listChat.lastIndex)
                }
            }
        }
        chatWithUserViewModel.getConversionMessageRef(conversionId)
        binding.rcChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    chatWithUserViewModel.loadMoreConversionMessageRef(conversionId)
                }
            }
        })
    }

    private val edtChangeListener = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            updateStatus(binding.viewGroupChat.edtMessage.text.toString())
        }

        override fun afterTextChanged(p0: Editable?) {
            handleTextChange(p0?.toString().orEmpty())
        }
    }

    private fun updateStatus(edtText: String) {
        if (edtText == "") {
            chatWithUserViewModel.updateStatus(
                Constants.KEY_STATE_ONLINE,
                mainViewModel.getUid()!!,
                Constants.KEY_COLLECTION_USER
            )
        } else {
            chatWithUserViewModel.updateStatus(
                Constants.KEY_STATUS_TYPING,
                mainViewModel.getUid()!!,
                Constants.KEY_COLLECTION_USER
            )
        }
    }

    override fun initAction() {
        binding.imgBack.click {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.viewGroupRecord.imgCloseRecord.click {
            stopRecording()
        }
        binding.tvName.click {
            navigateToViewProfile()
        }
        binding.imgAvatar.click {
            navigateToViewProfile()
        }
        initClickEdittext()
        setupViewSelectMore()
        initSendMessageClick()
        initClickOpenFile()
        initClickMic()
        initRecordingClick()
        initSettingClick()
        initClickTakePhotoToCamera()
        initEmojiClick()
        initUnBlockClick()
    }

    private fun initUnBlockClick() {
        binding.tvUnBlock.click {
            viewProfileViewModel.removeUserToBlock(receiverUid)
        }
    }

    private fun initEmojiClick() {
        val emoji = EmojiPopup(rootView = binding.root, editText = binding.viewGroupChat.edtMessage)
        var isClickIcon = false
        binding.viewGroupChat.imgIcon.setOnClickListener {
            isClickIcon = !isClickIcon
            emoji.toggle()
            if (isClickIcon) {
                binding.viewGroupChat.imgIcon.setImageResource(R.drawable.ic_icon_clicked)
            } else {
                binding.viewGroupChat.imgIcon.setImageResource(R.drawable.ic_icon)
            }
        }
    }

    private fun initClickTakePhotoToCamera() {
        binding.viewGroupChat.imgTakePhotoFromCamera.click {
            ImageUtils.takePhotoFromCamera(this)
        }
    }

    private fun setupViewSelectMore() {
        binding.viewGroupChat.imgSelectMore.click {
            binding.viewGroupChat.layoutChooseOption.visible()
            binding.viewGroupChat.imgSelectMore.gone()
        }
    }

    private fun initClickMic() {
        binding.viewGroupChat.imgMic.click {
//            if (ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.RECORD_AUDIO
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
//            } else {
//                binding.layoutChat.visible()
//                binding.layoutRecord.gone()
//            }
            requestRecordAudioPermission()
        }
    }

    private fun checkRecordAudioPermission(): Boolean {
        return context?.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordAudioPermission() {
        if (!checkRecordAudioPermission()) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            startRecording()
        }
    }

    private fun initSendMessageClick() {
        binding.viewGroupChat.vSendMessageUnEnable.click {
            val content = binding.viewGroupChat.edtMessage.text.toString()
            if (content.isNotEmpty()) {
                addOrUpdateConversion(content)
                val chatMessage = createChatMessage().apply {
                    typeMessage = Constants.TEXT
                    message = content
                }
                sendMessages(chatMessage, chatMessage.typeMessage!!)
                binding.viewGroupChat.edtMessage.setText("")
            }
        }
    }

    private fun initClickEdittext() {
        with(binding.viewGroupChat.edtMessage) {
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    onFocusEdittext()
                } else {
                    onNotFocusEdittext()
                }
            }
            addTextChangedListener(edtChangeListener)
            click {
                binding.viewGroupChat.apply {
                    binding.viewGroupChat.layoutChooseOption.gone()
                    binding.viewGroupChat.imgSelectMore.visible()
                }
            }
        }
    }

    private fun initRecordingClick() {
        binding.viewGroupRecord.vSendMessageUnEnable.click {
            stopRecording()
            val file = File("$dirPath$fileName.mp3")
            chatWithUserViewModel.uploadFileToFirebaseStorage(
                file,
                Constants.AUDIO
            ) { state ->
                when (state) {
                    is State.Loading -> binding.progressSendMessage.visible()
                    is State.Success -> {
                        addOrUpdateConversion(Constants.TYPE_LAST_MESSAGE_AUDIO)
                        val chatMessage = createChatMessage().apply {
                            typeMessage = Constants.AUDIO
                            message = state.data
                            duration = timeRecording
                        }
                        sendMessages(chatMessage, chatMessage.typeMessage!!)
                        binding.progressSendMessage.gone()
                    }

                    else -> {
                        requireActivity().showToast(state.toString())
                    }
                }

            }
        }
    }

    private fun initSettingClick() {
        binding.imgSettingChat.click {
            navigateToViewProfile()
        }
    }

    private fun navigateToViewProfile() {
        val bundle = Bundle()
        bundle.putString(Constants.KEY_RECEIVER_ID, receiverUid)
        bundle.putString(Constants.KEY_CONVERSION_ID, conversionId)
        openFragment(ViewProfileFragment::class.java, bundle, true)
    }

    private fun pushNotification() {

    }

    private fun initClickOpenFile() {
        binding.viewGroupChat.imgFile.click {
            ImageUtils.loadImages(this)
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startRecording()
            } else {
                ImageUtils.showSettingDialog(this)
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_GET_IMAGE_FROM_GALLERY && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                if (activity?.contentResolver?.getType(uri)
                        ?.startsWith(Constants.IMAGE) == true
                ) {
                    uploadImageAndSendMessage(uri)
                }
                if (activity?.contentResolver?.getType(uri)
                        ?.startsWith(Constants.VIDEO) == true
                ) {
                    uploadVideoAndSendMessage(uri)
                }
            }
        }
        if (requestCode == Constants.REQUEST_CODE_TAKE_PHOTO_FROM_CAMERA && resultCode == RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap
            uploadImageFromCameraAndSendMessage(bitmap)
        }
    }

    private fun startRecording() {
        binding.layoutChat.gone()
        binding.layoutRecord.visible()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            recorder = MediaRecorder(requireContext())
        } else {
            recorder = MediaRecorder()
        }.apply {
            dirPath = "${activity?.externalCacheDir?.absolutePath}/"
            fileName = "audio_record_${DateProvider.formatDate(Date())}"
            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile("$dirPath$fileName.mp3")
                try {
                    prepare()
                    start()
                } catch (e: Exception) {
                    e.message
                }
            }
        }
        timer.start()
    }

    private fun stopRecording() {
        timer.stop()
        with(recorder) {
            try {
                stop()
                reset()
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.layoutRecord.gone()
        binding.layoutChat.visible()
        amplitudes = binding.viewGroupRecord.waveForView.clear()
    }

    private fun sendMessages(messageModel: MessageModel, typeKey: String) {
        val message: HashMap<String, Any> = HashMap()
        message[Constants.KEY_CHAT_ID] = messageModel.id!!
        message[Constants.KEY_SENDER_ID] = messageModel.senderId!!
        message[Constants.KEY_TYPE_MESSAGE] = messageModel.typeMessage!!
        when (typeKey) {
            Constants.TEXT -> message[Constants.KEY_MESSAGE] =
                messageModel.message!!

            Constants.IMAGE -> message[Constants.KEY_MESSAGE] =
                messageModel.message!!

            Constants.AUDIO -> {
                message[Constants.KEY_MESSAGE] = messageModel.message!!
                message[Constants.KEY_DURATION] = messageModel.duration!!
            }

            Constants.VIDEO -> message[Constants.KEY_MESSAGE] =
                messageModel.message!!
        }
        message[Constants.KEY_TIMESTAMP] = messageModel.timestamp!!
        chatWithUserViewModel.addConversionMessageRef(conversionId, message) { state ->
            when (state) {
                is State.Success -> {
                    receiverUid?.let {
                        sendNotification(it)
                    }
                }

                is State.Error -> {
                    requireActivity().showToast("Can't send this message")
                }

                else -> {}
            }

        }
    }

    private fun uploadImageAndSendMessage(uri: Uri) {
        val iStream = activity?.contentResolver?.openInputStream(uri)
        val inputData = iStream?.let { ImageUtils.getBytes(it) }
        if (inputData != null) {
            chatWithUserViewModel.uploadImageToFirebaseStorage(inputData) { state ->
                when (state) {
                    is State.Loading -> binding.progressSendMessage.visible()
                    is State.Success -> {
                        addOrUpdateConversion(Constants.TYPE_LAST_MESSAGE_IMAGE)
                        val chatMessage = createChatMessage().apply {
                            typeMessage = Constants.IMAGE
                            message = state.data
                        }
                        sendMessages(chatMessage, chatMessage.typeMessage!!)
                        binding.progressSendMessage.gone()
                    }

                    else -> {
                        requireActivity().showToast(state.toString())
                    }
                }

            }
        }
    }

    private fun uploadVideoAndSendMessage(uri: Uri) {
        chatWithUserViewModel.uploadVideoToFirebaseStorage(uri) { state ->
            when (state) {
                is State.Loading -> binding.progressSendMessage.visible()
                is State.Success -> {
                    addOrUpdateConversion(Constants.TYPE_LAST_MESSAGE_VIDEO)
                    val chatMessage = createChatMessage().apply {
                        typeMessage = Constants.VIDEO
                        message = state.data
                    }
                    sendMessages(chatMessage, chatMessage.typeMessage!!)
                    binding.progressSendMessage.gone()
                }

                else -> {
                    requireActivity().showToast(state.toString())
                }
            }
        }
    }

    private fun uploadImageFromCameraAndSendMessage(bitmap: Bitmap) {
        val content = ImageUtils.convertBitmapToByteArray(bitmap)
        chatWithUserViewModel.uploadImageToFirebaseStorage(content) { state ->
            when (state) {
                is State.Loading -> binding.progressSendMessage.visible()
                is State.Success -> {
                    addOrUpdateConversion(Constants.TYPE_LAST_MESSAGE_IMAGE)
                    val chatMessage = createChatMessage().apply {
                        typeMessage = Constants.IMAGE
                        message = state.data
                    }
                    sendMessages(chatMessage, chatMessage.typeMessage!!)
                    binding.progressSendMessage.gone()
                }

                else -> {
                    requireActivity().showToast(state.toString())
                }
            }
        }
    }

    private fun createChatMessage(): MessageModel {
        return MessageModel(
            id = UUID.randomUUID().toString(),
            senderId = mainViewModel.getUid()!!,
            timestamp = Timestamp.now()
        )
    }

    private fun handleTextChange(content: String) {
        if (content.isEmpty()) {
            binding.viewGroupChat.vSendMessageUnEnable.gone()
        } else {
            binding.viewGroupChat.vSendMessageUnEnable.visible()
        }
    }

    private fun onFocusEdittext() {
        binding.viewGroupChat.layoutChooseOption.gone()
        binding.viewGroupChat.imgSelectMore.visible()
    }

    private fun onNotFocusEdittext() {
        binding.viewGroupChat.layoutChooseOption.visible()
        binding.viewGroupChat.imgSelectMore.gone()
    }

    override fun onTimeTick(duration: String) {
        timeRecording = duration
        binding.viewGroupRecord.tvTimer.text = duration
        binding.viewGroupRecord.waveForView.addAmplitudes(recorder.maxAmplitude.toFloat())
    }

    private fun sendNotification(receiverId: String) {
        chatWithUserViewModel.createNotification(
            "send 1 message",
            "message",
            receiverId
        ) {
//            val listUidMute = getListUidNeedMute().toMutableList()
            val objectTurnoff =
                Turnofnotification(it, emptyList<MuteNotification>().toMutableList())
            val hien = Json.encodeToString(objectTurnoff)
            chatWithUserViewModel.getUserid()?.let { it1 -> sharedPreferences.setString(it1, hien) }
            chatWithUserViewModel.sendNotification(it)
        }
    }

    private fun getListUidNeedMute(): List<MuteNotification> {
        val listUid: ArrayList<MuteNotification>
        val serializedObject = sharedPreferences.getString(Constants.KEY_LIST_UID_NEED_MUTE)
        if (serializedObject != null) {
            val gson = Gson()
            val type = object : TypeToken<List<MuteNotification?>?>() {}.type
            listUid = gson.fromJson(serializedObject, type)
            return listUid
        }
        return emptyList()
    }
}
