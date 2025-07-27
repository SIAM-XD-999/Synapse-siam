package com.synapse.social.studioasinc

import android.Manifest
import android.animation.*
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.content.res.*
import android.graphics.*
import android.graphics.drawable.*
import android.media.*
import android.net.Uri
import android.os.*
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.util.*
import android.view.*
import android.view.animation.*
import android.webkit.*
import android.widget.*
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.color.MaterialColors
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.synapse.social.studioasinc.CenterCropLinearLayoutNoEffect
import com.synapse.social.studioasinc.FadeEditText
import com.theartofdev.edmodo.cropper.*
import com.yalantis.ucrop.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern
import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import androidx.browser.customtabs.CustomTabsIntent
import com.synapse.social.studioasinc.styling.TextStylingUtil
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator

class ChatActivity : AppCompatActivity() {

    val REQ_CD_IMAGE_PICKER = 101

    private val _timer = Timer()
    private val _firebase = FirebaseDatabase.getInstance()
    private val _firebase_storage = FirebaseStorage.getInstance()

    private var SynapseLoadingDialog: ProgressDialog? = null
    private var AudioMessageRecorder: MediaRecorder? = null
    private var ChatMessagesLimit = 0.0
    private var ChatSendMap = HashMap<String, Any>()
    private var ChatInboxSend = HashMap<String, Any>()
    private var recordMs = 0.0
    private var ChatInboxSend2 = HashMap<String, Any>()
    private var SecondUserAvatar = ""
    private var typingSnd = HashMap<String, Any>()
    private var ReplyMessageID = ""
    private var SecondUserName = ""
    private var FirstUserName = ""
    private var ChatInitialSize = 0
    private var object_clicked = ""
    private var handle = ""
    private var block = HashMap<String, Any>()
    private var block_switch = 0.0
    private var file = ""
    private var filename = ""
    private var file_type_expand = 0.0
    private var AddFromUrlStr = ""
    private var IMG_BB_API_KEY = ""
    private var imageUrl = ""
    private var AndroidDevelopersBlogURL = ""

    private val ChatMessagesList = ArrayList<HashMap<String, Any>>()

    private lateinit var body: CenterCropLinearLayoutNoEffect
    private lateinit var top: CenterCropLinearLayoutNoEffect
    private lateinit var middle: LinearLayout
    // Removed bottomSpace declaration as it's not in XML anymore
    private lateinit var mMessageReplyLayout: MaterialCardView
    private lateinit var message_input_overall_container: LinearLayout
    private lateinit var bottomAudioRecorder: MaterialCardView
    private lateinit var unblock_btn: Button
    private lateinit var blocked_txt: TextView
    private lateinit var chat_toolbar: MaterialToolbar
    private lateinit var topProfileLayout: LinearLayout
    private lateinit var topProfileLayoutSpace: LinearLayout
    private lateinit var topProfileCard: CardView
    private lateinit var topProfileLayoutRight: LinearLayout
    private lateinit var topProfileLayoutProfileImage: ShapeableImageView
    private lateinit var topProfileLayoutRightTop: LinearLayout
    private lateinit var topProfileLayoutStatus: TextView
    private lateinit var topProfileLayoutUsername: TextView
    private lateinit var topProfileLayoutGenderBadge: ImageView
    private lateinit var topProfileLayoutVerifiedBadge: ImageView
    private lateinit var bannedUserInfo: LinearLayout
    private lateinit var ChatMessagesListRecycler: RecyclerView
    private lateinit var noChatText: TextView
    private lateinit var bannedUserInfoIc: ImageView
    private lateinit var bannedUserInfoText: TextView
    private lateinit var mMessageReplyLayoutBody: LinearLayout // Re-enabled based on XML
    private lateinit var mMessageReplyLayoutSpace: LinearLayout // Re-enabled based on XML
    private lateinit var mMessageReplyLayoutBodyIc: ImageView
    private lateinit var mMessageReplyLayoutBodyRight: LinearLayout
    private lateinit var mMessageReplyLayoutBodyCancel: ImageView
    private lateinit var mMessageReplyLayoutBodyRightUsername: TextView
    private lateinit var mMessageReplyLayoutBodyRightMessage: TextView
    private lateinit var message_input_outlined_round: MaterialCardView
    private lateinit var send_round_btn: FloatingActionButton
    private lateinit var message_et: FadeEditText
    private lateinit var gallery_btn: ImageView
    private lateinit var attachment_btn: ImageView
    private lateinit var send_type_voice_btn: ImageView
    private lateinit var bottomAudioRecorderCancel: ImageView
    private lateinit var bottomAudioRecorderTime: TextView
    private lateinit var bottomAudioRecorderSend: FloatingActionButton
    private lateinit var blocked_info_card: MaterialCardView

    private lateinit var selected_attachments_container: LinearLayout
    private lateinit var selected_images_recycler: RecyclerView
    private lateinit var overall_upload_prog: LinearProgressIndicator

    private val selectedImagesPaths = ArrayList<String>()
    private val uploadProgressMap = HashMap<String, Int>()

    private lateinit var main: DatabaseReference
    private var _main_child_listener: ChildEventListener? = null
    private lateinit var auth: FirebaseAuth
    private var _auth_create_user_listener: OnCompleteListener<AuthResult>? = null
    private var _auth_sign_in_listener: OnCompleteListener<AuthResult>? = null
    private var _auth_reset_password_listener: OnCompleteListener<Void>? = null
    private var auth_updateEmailListener: OnCompleteListener<Void>? = null
    private var auth_updatePasswordListener: OnCompleteListener<Void>? = null
    private var auth_emailVerificationSentListener: OnCompleteListener<Void>? = null
    private var auth_deleteUserListener: OnCompleteListener<Void>? = null
    private var auth_updateProfileListener: OnCompleteListener<Void>? = null
    private var auth_phoneAuthListener: OnCompleteListener<AuthResult>? = null
    private var auth_googleSignInListener: OnCompleteListener<AuthResult>? = null
    private var loadTimer: TimerTask? = null
    private lateinit var cc: Calendar
    private lateinit var vbr: Vibrator
    private var timer: TimerTask? = null
    private lateinit var blocklist: DatabaseReference
    private var _blocklist_child_listener: ChildEventListener? = null
    private lateinit var blocked: SharedPreferences
    private lateinit var image_picker: Intent
    private lateinit var theme: SharedPreferences
    private var cd: AlertDialog? = null
    private lateinit var upload_selected_img: StorageReference
    private var _upload_selected_img_upload_success_listener: OnCompleteListener<Uri>? = null
    private var _upload_selected_img_download_success_listener: OnSuccessListener<FileDownloadTask.TaskSnapshot>? = null
    private var _upload_selected_img_upload_progress_listener: OnProgressListener<UploadTask.TaskSnapshot>? = null
    private var _upload_selected_img_download_progress_listener: OnProgressListener<FileDownloadTask.TaskSnapshot>? = null
    private var _upload_selected_img_failure_listener: OnFailureListener? = null
    private lateinit var i: Intent
    private lateinit var zorry: AlertDialog.Builder
    private lateinit var appSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat)
        FirebaseApp.initializeApp(this) // Initialize Firebase as early as possible

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
        } else {
            // Only initialize views and logic if permissions are granted or not needed yet
            initialize(savedInstanceState)
            initializeLogic()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            // Re-check permission after request
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                initialize(null) // Initialize if it wasn't already
                initializeLogic()
            } else {
                SketchwareUtil.showMessage(applicationContext, "Permissions required for full functionality.")
                // Potentially disable features that rely on these permissions
            }
        }
    }

    private fun initialize(savedInstanceState: Bundle?) {
        body = findViewById(R.id.body)
        top = findViewById(R.id.top)
        middle = findViewById(R.id.middle)
        // Removed bottomSpace, not in XML
        mMessageReplyLayout = findViewById(R.id.mMessageReplyLayout)
        message_input_overall_container = findViewById(R.id.message_input_overall_container)
        bottomAudioRecorder = findViewById(R.id.bottomAudioRecorder)
        unblock_btn = findViewById(R.id.unblock_btn)
        blocked_txt = findViewById(R.id.blocked_txt)
        chat_toolbar = findViewById(R.id.chat_toolbar)
        topProfileLayout = findViewById(R.id.topProfileLayout)
        topProfileLayoutSpace = findViewById(R.id.topProfileLayoutSpace)
        topProfileCard = findViewById(R.id.topProfileCard)
        topProfileLayoutRight = findViewById(R.id.topProfileLayoutRight)
        topProfileLayoutProfileImage = findViewById(R.id.topProfileLayoutProfileImage)
        topProfileLayoutRightTop = findViewById(R.id.topProfileLayoutRightTop)
        topProfileLayoutStatus = findViewById(R.id.topProfileLayoutStatus)
        topProfileLayoutUsername = findViewById(R.id.topProfileLayoutUsername)
        topProfileLayoutGenderBadge = findViewById(R.id.topProfileLayoutGenderBadge)
        topProfileLayoutVerifiedBadge = findViewById(R.id.topProfileLayoutVerifiedBadge)
        bannedUserInfo = findViewById(R.id.bannedUserInfo)
        ChatMessagesListRecycler = findViewById(R.id.ChatMessagesListRecycler)
        noChatText = findViewById(R.id.noChatText)
        bannedUserInfoIc = findViewById(R.id.bannedUserInfoIc)
        bannedUserInfoText = findViewById(R.id.bannedUserInfoText)
        mMessageReplyLayoutBody = findViewById(R.id.mMessageReplyLayoutBody) // Un-commented
        mMessageReplyLayoutSpace = findViewById(R.id.mMessageReplyLayoutSpace) // Un-commented
        mMessageReplyLayoutBodyIc = findViewById(R.id.mMessageReplyLayoutBodyIc)
        mMessageReplyLayoutBodyRight = findViewById(R.id.mMessageReplyLayoutBodyRight)
        mMessageReplyLayoutBodyCancel = findViewById(R.id.mMessageReplyLayoutBodyCancel)
        mMessageReplyLayoutBodyRightUsername = findViewById(R.id.mMessageReplyLayoutBodyRightUsername)
        mMessageReplyLayoutBodyRightMessage = findViewById(R.id.mMessageReplyLayoutBodyRightMessage)
        message_input_outlined_round = findViewById(R.id.message_input_outlined_round)
        send_round_btn = findViewById(R.id.send_round_btn)
        message_et = findViewById(R.id.message_et)
        gallery_btn = findViewById(R.id.gallery_btn)
        attachment_btn = findViewById(R.id.attachment_btn)
        send_type_voice_btn = findViewById(R.id.send_type_voice_btn)
        bottomAudioRecorderCancel = findViewById(R.id.bottomAudioRecorderCancel)
        bottomAudioRecorderTime = findViewById(R.id.bottomAudioRecorderTime)
        bottomAudioRecorderSend = findViewById(R.id.bottomAudioRecorderSend)
        blocked_info_card = findViewById(R.id.blocked_info_card)

        selected_attachments_container = findViewById(R.id.selected_attachments_container)
        selected_images_recycler = findViewById(R.id.selected_images_recycler)
        overall_upload_prog = findViewById(R.id.overall_upload_prog)

        auth = FirebaseAuth.getInstance()
        vbr = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        blocked = getSharedPreferences("block", Activity.MODE_PRIVATE)
        image_picker = Intent(Intent.ACTION_GET_CONTENT)
        image_picker.type = "image/*"
        image_picker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        theme = getSharedPreferences("theme", Activity.MODE_PRIVATE)
        zorry = AlertDialog.Builder(this)
        appSettings = getSharedPreferences("appSettings", Activity.MODE_PRIVATE)

        main = _firebase.getReference("skyline")
        blocklist = _firebase.getReference("skyline/blocklist")
        upload_selected_img = _firebase_storage.getReference("synapse/chats/images")
        i = Intent()
        cc = Calendar.getInstance()

        setSupportActionBar(chat_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        selected_images_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        selected_images_recycler.adapter = SelectedImagesAdapter(selectedImagesPaths, uploadProgressMap)

        unblock_btn.setOnClickListener { _Unblock_this_user() }

        chat_toolbar.setNavigationOnClickListener { onBackPressed() }

        topProfileLayout.setOnClickListener {
            i.setClass(applicationContext, Chat2ndUserMoreSettingsActivity::class.java)
            i.putExtra("uid", this.intent.getStringExtra("uid"))
            startActivity(i)
        }

        mMessageReplyLayoutBodyCancel.setOnClickListener {
            ReplyMessageID = "null"
            mMessageReplyLayout.visibility = View.GONE
            vbr.vibrate(48L)
        }

        message_input_outlined_round.setOnClickListener { message_et.requestFocus() }

        send_round_btn.setOnClickListener { _send_btn() }

        message_et.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(param1: CharSequence, param2: Int, param3: Int, param4: Int) {
                val _charSeq = param1.toString()
                if (selectedImagesPaths.isNotEmpty() || _charSeq.isNotEmpty()) {
                    send_round_btn.setImageResource(R.drawable.ic_send_48px)
                    // Removed orientation setting for MaterialCardView
                    FirebaseDatabase.getInstance().getReference("skyline/chats").child(this@ChatActivity.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child("typing-message").updateChildren(typingSnd)
                } else {
                    send_round_btn.setImageResource(R.drawable.ic_thumb_up_48px)
                    // Removed orientation setting for MaterialCardView
                    FirebaseDatabase.getInstance().getReference("skyline/chats").child(this@ChatActivity.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child("typing-message").removeValue()
                }

                _TransitionManager(message_input_overall_container, 125.0)
                typingSnd = HashMap()
                typingSnd["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
                typingSnd["typingMessageStatus"] = "true"
            }

            override fun beforeTextChanged(param1: CharSequence, param2: Int, param3: Int, param4: Int) {

            }

            override fun afterTextChanged(param1: Editable) {

            }
        })

        gallery_btn.setOnClickListener {
            image_picker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(image_picker, REQ_CD_IMAGE_PICKER)
        }

        attachment_btn.setOnClickListener {
            image_picker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(image_picker, REQ_CD_IMAGE_PICKER)
        }

        send_type_voice_btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1000)
                } else {
                    _AudioRecorderStart()
                }
            } else {
                _AudioRecorderStart()
            }
        }

        bottomAudioRecorderCancel.setOnClickListener { _AudioRecorderStop() }

        bottomAudioRecorderTime.setOnClickListener { }

        _main_child_listener = object : ChildEventListener {
            override fun onChildAdded(param1: DataSnapshot, param2: String?) {
                val _ind = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                val _childKey = param1.key
                val _childValue = param1.getValue(_ind)

            }

            override fun onChildChanged(param1: DataSnapshot, param2: String?) {
                val _ind = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                val _childKey = param1.key
                val _childValue = param1.getValue(_ind)

            }

            override fun onChildMoved(param1: DataSnapshot, param2: String?) {

            }

            override fun onChildRemoved(param1: DataSnapshot) {
                val _ind = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                val _childKey = param1.key
                val _childValue = param1.getValue(_ind)

            }

            override fun onCancelled(param1: DatabaseError) {
                val _errorCode = param1.code
                val _errorMessage = param1.message

            }
        }
        main.addChildEventListener(_main_child_listener!!)

        _blocklist_child_listener = object : ChildEventListener {
            override fun onChildAdded(param1: DataSnapshot, param2: String?) {
                val _ind = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                val _childValue = param1.getValue(_ind)
                if (_childValue == null) return

                val targetUid = this@ChatActivity.intent.getStringExtra("uid")
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                if (param1.key == targetUid) {
                    if (_childValue.containsKey(currentUserUid)) {
                        message_input_overall_container.visibility = View.GONE
                        blocked_info_card.visibility = View.VISIBLE
                        unblock_btn.visibility = View.GONE
                    } else {
                        message_input_overall_container.visibility = View.VISIBLE
                        blocked_info_card.visibility = View.GONE
                    }
                } else if (param1.key == currentUserUid) {
                    if (_childValue.containsKey(targetUid)) {
                        message_input_overall_container.visibility = View.GONE
                        unblock_btn.visibility = View.VISIBLE
                        blocked_info_card.visibility = View.GONE
                    } else {
                        message_input_overall_container.visibility = View.VISIBLE
                        unblock_btn.visibility = View.GONE
                    }
                }
            }

            override fun onChildChanged(param1: DataSnapshot, param2: String?) {
                val _ind = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                val _childValue = param1.getValue(_ind)
                if (_childValue == null) return

                val targetUid = this@ChatActivity.intent.getStringExtra("uid")
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                if (param1.key == targetUid) {
                    if (_childValue.containsKey(currentUserUid)) {
                        message_input_overall_container.visibility = View.GONE
                        blocked_info_card.visibility = View.VISIBLE
                        unblock_btn.visibility = View.GONE
                    } else {
                        message_input_overall_container.visibility = View.VISIBLE
                        blocked_info_card.visibility = View.GONE
                    }
                } else if (param1.key == currentUserUid) {
                    if (_childValue.containsKey(targetUid)) {
                        message_input_overall_container.visibility = View.GONE
                        unblock_btn.visibility = View.VISIBLE
                        blocked_info_card.visibility = View.GONE
                    } else {
                        message_input_overall_container.visibility = View.VISIBLE
                        unblock_btn.visibility = View.GONE
                    }
                }
            }

            override fun onChildMoved(param1: DataSnapshot, param2: String?) {

            }

            override fun onChildRemoved(param1: DataSnapshot) {
                val _ind = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                val _childKey = param1.key
                val _childValue = param1.getValue(_ind)

            }

            override fun onCancelled(param1: DatabaseError) {
                val _errorCode = param1.code
                val _errorMessage = param1.message

            }
        }
        blocklist.addChildEventListener(_blocklist_child_listener!!)

        _upload_selected_img_upload_progress_listener = OnProgressListener { param1 ->
            val _progressValue = (100.0 * param1.bytesTransferred) / param1.totalByteCount
            overall_upload_prog.progress = _progressValue.toInt()
            overall_upload_prog.visibility = View.VISIBLE
            _LoadingDialog(true)
        }

        _upload_selected_img_download_progress_listener = OnProgressListener { param1 ->
            val _progressValue = (100.0 * param1.bytesTransferred) / param1.totalByteCount

        }

        _upload_selected_img_upload_success_listener = OnCompleteListener { param1 ->
            val _downloadUrl = param1.result.toString()
            _LoadingDialog(false)
            cc = Calendar.getInstance()
            val uniqueMessageKey = main.push().key
            ChatSendMap = HashMap()
            ChatSendMap["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
            ChatSendMap["TYPE"] = "MESSAGE"
            ChatSendMap["message_text"] = message_et.text.toString().trim()
            ChatSendMap["message_image_uris"] = _downloadUrl
            ChatSendMap["message_state"] = "sended"
            if (ReplyMessageID != "null") {
                ChatSendMap["replied_message_id"] = ReplyMessageID
            }
            ChatSendMap["key"] = uniqueMessageKey!!
            ChatSendMap["push_date"] = cc.timeInMillis.toString()
            FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().currentUser!!.uid).child(this@ChatActivity.intent.getStringExtra("uid")!!).child(uniqueMessageKey).updateChildren(ChatSendMap)
            FirebaseDatabase.getInstance().getReference("skyline/chats").child(this@ChatActivity.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child(uniqueMessageKey).updateChildren(ChatSendMap)
            ChatInboxSend = HashMap()
            ChatInboxSend["uid"] = this@ChatActivity.intent.getStringExtra("uid")!!
            ChatInboxSend["TYPE"] = "MESSAGE"
            ChatInboxSend["last_message_uid"] = FirebaseAuth.getInstance().currentUser!!.uid
            ChatInboxSend["last_message_text"] = message_et.text.toString().trim()
            ChatInboxSend["last_message_state"] = "sended"
            ChatInboxSend["push_date"] = cc.timeInMillis.toString()
            FirebaseDatabase.getInstance().getReference("skyline/inbox").child(FirebaseAuth.getInstance().currentUser!!.uid).child(this@ChatActivity.intent.getStringExtra("uid")!!).updateChildren(ChatInboxSend)
            ChatInboxSend2 = HashMap()
            ChatInboxSend2["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
            ChatInboxSend2["TYPE"] = "MESSAGE"
            ChatInboxSend2["last_message_uid"] = FirebaseAuth.getInstance().currentUser!!.uid
            ChatInboxSend2["last_message_text"] = message_et.text.toString().trim()
            ChatInboxSend2["last_message_state"] = "sended"
            ChatInboxSend2["push_date"] = cc.timeInMillis.toString()
            FirebaseDatabase.getInstance().getReference("skyline/inbox").child(this@ChatActivity.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(ChatInboxSend2)
            message_et.setText("")
            filename = ""
            file = ""
            selectedImagesPaths.clear()
            uploadProgressMap.clear()
            updateSelectedImagesUI()
            file_type_expand = 0.0

            FirebaseDatabase.getInstance().getReference("skyline/chats").child(this@ChatActivity.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child("typing-message").removeValue()
            ChatSendMap.clear()
            ChatInboxSend.clear()
            ChatInboxSend2.clear()
            ReplyMessageID = "null"
            mMessageReplyLayout.visibility = View.GONE
        }

        _upload_selected_img_download_success_listener = OnSuccessListener { param1 ->
            val _totalByteCount = param1.totalByteCount

        }

        _upload_selected_img_failure_listener = OnFailureListener { param1 ->
            val _message = param1.message

        }

        auth_updateEmailListener = OnCompleteListener { param1 ->
            val _success = param1.isSuccessful
            val _errorMessage = if (param1.exception != null) param1.exception!!.message else ""

        }

        auth_updatePasswordListener = OnCompleteListener { param1 ->
            val _success = param1.isSuccessful
            val _errorMessage = if (param1.exception != null) param1.exception!!.message else ""

        }

        auth_emailVerificationSentListener = OnCompleteListener { param1 ->
            val _success = param1.isSuccessful
            val _errorMessage = if (param1.exception != null) param1.exception!!.message else ""

        }

        auth_deleteUserListener = OnCompleteListener { param1 ->
            val _success = param1.isSuccessful
            val _errorMessage = if (param1.exception != null) param1.exception!!.message else ""

        }

        auth_phoneAuthListener = OnCompleteListener { task ->
            val _success = task.isSuccessful
            val _errorMessage = if (task.exception != null) task.exception!!.message else ""

        }

        auth_updateProfileListener = OnCompleteListener { param1 ->
            val _success = param1.isSuccessful
            val _errorMessage = if (param1.exception != null) param1.exception!!.message else ""

        }

        auth_googleSignInListener = OnCompleteListener { task ->
            val _success = task.isSuccessful
            val _errorMessage = if (task.exception != null) task.exception!!.message else ""

        }

        _auth_create_user_listener = OnCompleteListener { param1 ->
            val _success = param1.isSuccessful
            val _errorMessage = if (param1.exception != null) param1.exception!!.message else ""

        }

        _auth_sign_in_listener = OnCompleteListener { param1 ->
            val _success = param1.isSuccessful
            val _errorMessage = if (param1.exception != null) param1.exception!!.message else ""

        }

        _auth_reset_password_listener = OnCompleteListener { param1 ->
            val _success = param1.isSuccessful

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_video_call -> {
                return true
            }
            R.id.action_call -> {
                return true
            }
            R.id.action_info -> {
                i.setClass(applicationContext, Chat2ndUserMoreSettingsActivity::class.java)
                i.putExtra("uid", this.intent.getStringExtra("uid"))
                startActivity(i)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeLogic() {
        if (message_et.text.toString().trim().isEmpty()) {
            _TransitionManager(message_input_overall_container, 250.0)
        } else {
            _TransitionManager(message_input_overall_container, 250.0)
        }
        SecondUserAvatar = "null"
        ReplyMessageID = "null"
        ChatMessagesLimit = 80.0
        file_type_expand = 0.0
        block_switch = 0.0
        bannedUserInfo.elevation = 2f
        val ChatRecyclerLayoutManager = LinearLayoutManager(this)
        ChatRecyclerLayoutManager.reverseLayout = false
        ChatRecyclerLayoutManager.stackFromEnd = true
        ChatMessagesListRecycler.layoutManager = ChatRecyclerLayoutManager
        ChatMessagesListRecycler.adapter = ChatMessagesListRecyclerAdapter(ChatMessagesList)
        _getUserReference()
        ChatMessagesListRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(@NonNull ChatMessagesListRecycler: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(ChatMessagesListRecycler, dx, dy)

                val layoutManager = ChatMessagesListRecycler.layoutManager as LinearLayoutManager

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                    val childView = layoutManager.findViewByPosition(i)
                    if (childView != null) {
                        val percentage = calculatePercentage(layoutManager, childView)
                        animateView(childView, percentage)
                    }
                }
            }

            private fun calculatePercentage(layoutManager: LinearLayoutManager, childView: View): Float {
                val itemHeight = childView.height
                val parentHeight = layoutManager.height
                val top = childView.top
                val bottom = childView.bottom

                if (parentHeight == 0) {
                    return 0f
                }

                val visibleHeight = Math.min(bottom, parentHeight) - Math.max(top, 0)
                val totalHeight = Math.min(itemHeight, parentHeight)

                return visibleHeight.toFloat() / totalHeight
            }

            private fun animateView(view: View, percentage: Float) {
                view.alpha = percentage
            }
        })

        blocked_txt.text = Html.fromHtml("<p>You can't reply to this conversation. <a href=\"https://example.com/learn-more\" style=\"color: #2962FF;\"><b>Learn more</b></a></p>")
        blocked_info_card.visibility = View.GONE
        unblock_btn.visibility = View.GONE

        selected_attachments_container.visibility = View.GONE
        overall_upload_prog.visibility = View.GONE

        attachment_btn.visibility = View.GONE
        send_type_voice_btn.visibility = View.GONE

        _stateColor(-0x1, -0x1)
        _ScrollingText(topProfileLayoutUsername)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CD_IMAGE_PICKER -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        if (data.clipData != null) {
                            for (idx in 0 until data.clipData!!.itemCount) {
                                val uri = data.clipData!!.getItemAt(idx).uri
                                FileUtil.convertUriToFilePath(applicationContext, uri)?.let { filePath ->
                                    selectedImagesPaths.add(filePath)
                                }
                            }
                        } else if (data.data != null) {
                            val uri = data.data
                            FileUtil.convertUriToFilePath(applicationContext, uri)?.let { filePath ->
                                selectedImagesPaths.add(filePath)
                            }
                        }
                    }
                    updateSelectedImagesUI()
                }
            }
        }
    }

    private fun updateSelectedImagesUI() {
        if (selectedImagesPaths.isNotEmpty()) {
            selected_attachments_container.visibility = View.VISIBLE
            (selected_images_recycler.adapter as? SelectedImagesAdapter)?.notifyDataSetChanged()
            send_round_btn.setImageResource(R.drawable.ic_send_48px)
        } else {
            selected_attachments_container.visibility = View.GONE
            if (message_et.text.isEmpty()) {
                send_round_btn.setImageResource(R.drawable.ic_thumb_up_48px)
            }
        }
        _TransitionManager(message_input_overall_container, 150.0)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onPause() {
        super.onPause()
        FirebaseDatabase.getInstance().getReference("skyline/chats").child(this.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child("typing-message").removeValue()
    }

    override fun onStop() {
        super.onStop()
        FirebaseDatabase.getInstance().getReference("skyline/chats").child(this.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child("typing-message").removeValue()
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseDatabase.getInstance().getReference("skyline/chats").child(this.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child("typing-message").removeValue()
    }

    fun _stateColor( _statusColor: Int, _navigationColor: Int) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = _statusColor
        window.navigationBarColor = _navigationColor
    }

    fun _viewGraphics( _view: View, _onFocus: Int, _onRipple: Int, _radius: Double, _stroke: Double, _strokeColor: Int) {
        val GG = GradientDrawable()
        GG.setColor(_onFocus)
        GG.setCornerRadius(_radius.toFloat())
        GG.setStroke(_stroke.toInt(), _strokeColor)
        val RE = RippleDrawable(ColorStateList(arrayOf(intArrayOf()), intArrayOf(_onRipple)), GG, null)
        _view.background = RE
    }

    fun _ImageColor( _image: ImageView, _color: Int) {
        _image.setColorFilter(_color, PorterDuff.Mode.SRC_ATOP)
    }

    fun _messageOverviewPopup( _view: View, _position: Double, _data: ArrayList<HashMap<String, Any>>) {
        val pop1V = layoutInflater.inflate(R.layout.chat_msg_options_popup_cv_synapse, null)
        val pop1 = PopupWindow(pop1V, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        pop1.isFocusable = true
        pop1.inputMethodMode = ListPopupWindow.INPUT_METHOD_NOT_NEEDED
        val main = pop1V.findViewById<LinearLayout>(R.id.main)
        val edit = pop1V.findViewById<LinearLayout>(R.id.edit)
        val reply = pop1V.findViewById<LinearLayout>(R.id.reply)
        val copy = pop1V.findViewById<LinearLayout>(R.id.copy)
        val delete = pop1V.findViewById<LinearLayout>(R.id.delete)
        pop1.animationStyle = android.R.style.Animation_Dialog
        val location = IntArray(2)
        val anchorView = _view
        anchorView.getLocationOnScreen(location)

        val screenHeight = applicationContext.resources.displayMetrics.heightPixels
        val halfScreenHeight = screenHeight / 2
        val anchorViewHeight = anchorView.height

        if (location[1] < halfScreenHeight) {
            pop1.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] + anchorViewHeight)
        } else if (location[1] > halfScreenHeight) {
            pop1.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] - pop1.height)
        } else {
            pop1.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] + anchorViewHeight / 2 - pop1.height / 2)
        }
        if (_data[_position.toInt()]["uid"].toString() == FirebaseAuth.getInstance().currentUser!!.uid) {
            main.gravity = Gravity.CENTER or Gravity.RIGHT
            edit.visibility = View.VISIBLE
            delete.visibility = View.VISIBLE
        } else {
            main.gravity = Gravity.CENTER or Gravity.LEFT
            edit.visibility = View.GONE
            delete.visibility = View.GONE
        }
        _viewGraphics(edit, -0x1, -0x111112, 0.0, 0.0, -0x1)
        _viewGraphics(reply, -0x1, -0x111112, 0.0, 0.0, -0x1)
        _viewGraphics(copy, -0x1, -0x111112, 0.0, 0.0, -0x1)
        _viewGraphics(delete, -0x1, -0x111112, 0.0, 0.0, -0x1)
        main.setOnClickListener { pop1.dismiss() }
        reply.setOnClickListener {
            ReplyMessageID = _data[_position.toInt()]["key"].toString()
            if (_data[_position.toInt()]["uid"].toString() == FirebaseAuth.getInstance().currentUser!!.uid) {
                mMessageReplyLayoutBodyRightUsername.text = FirstUserName
            } else {
                mMessageReplyLayoutBodyRightUsername.text = SecondUserName
            }
            mMessageReplyLayoutBodyRightMessage.text = _data[_position.toInt()]["message_text"].toString()
            mMessageReplyLayout.visibility = View.VISIBLE
            vbr.vibrate(48L)
            pop1.dismiss()
        }
        copy.setOnClickListener {
            (getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager).setPrimaryClip(ClipData.newPlainText("clipboard", _data[_position.toInt()]["message_text"].toString()))
            vbr.vibrate(48L)
            pop1.dismiss()
        }
        delete.setOnClickListener {
            _DeleteMessageDialog(_data, _position)
            pop1.dismiss()
        }
        edit.setOnClickListener {
            pop1.dismiss()
            cd = AlertDialog.Builder(this@ChatActivity).create()
            val cdLI = layoutInflater
            val cdCV = cdLI.inflate(R.layout.add_image_from_url_dialog, null)
            cd!!.setView(cdCV)
            val add_button = cdCV.findViewById<LinearLayout>(R.id.add_button)
            val image_url_input = cdCV.findViewById<EditText>(R.id.image_url_input)
            val image_url_input_inputlayout = cdCV.findViewById<TextInputLayout>(R.id.image_url_input_inputlayout)
            val dialog_top_title = cdCV.findViewById<TextView>(R.id.dialog_top_title)
            val subtitle = cdCV.findViewById<TextView>(R.id.subtitle)
            val textview3 = cdCV.findViewById<TextView>(R.id.textview3)
            cd!!.setCancelable(true)
            cd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            cd!!.show()
            dialog_top_title.text = "Edit message"
            subtitle.text = "The updated message can be reported later so that we can take action based on it."
            textview3.text = "Update"
            image_url_input.setText(_data[_position.toInt()]["message_text"].toString())
            _viewGraphics(add_button, resources.getColor(R.color.colorPrimary), -0x2171c4, 300.0, 0.0, Color.TRANSPARENT)
            image_url_input.maxLines = 8
            image_url_input_inputlayout.setBoxStrokeColor(-0xb0b3ac)
            image_url_input_inputlayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            image_url_input_inputlayout.setBoxCornerRadii(50f, 50f, 50f, 50f)
            image_url_input_inputlayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            add_button.setOnClickListener {
                if (!(image_url_input.text.toString().length == 0)) {
                    ChatSendMap = HashMap()
                    ChatSendMap["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
                    ChatSendMap["TYPE"] = "MESSAGE"
                    ChatSendMap["message_uid"] = FirebaseAuth.getInstance().currentUser!!.uid
                    ChatSendMap["message_text"] = image_url_input.text.toString().trim()
                    ChatSendMap["message_state"] = "sended"
                    ChatSendMap["push_date"] = cc.timeInMillis.toString()
                    FirebaseDatabase.getInstance().getReference("skyline/chats")
                        .child(this@ChatActivity.intent.getStringExtra("uid")!!)
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(_data[_position.toInt()]["key"].toString())
                        .updateChildren(ChatSendMap)
                    FirebaseDatabase.getInstance().getReference("skyline/chats")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(this@ChatActivity.intent.getStringExtra("uid")!!)
                        .child(_data[_position.toInt()]["key"].toString())
                        .updateChildren(ChatSendMap)
                    cd!!.dismiss()
                } else {
                    SketchwareUtil.showMessage(applicationContext, "Can't be empty")
                }
            }
        }
    }

    fun _setMargin( _view: View, _r: Double, _l: Double, _t: Double, _b: Double) {
        val dpRatio = c(this).getContext().resources.displayMetrics.density
        val right = (_r * dpRatio).toInt()
        val left = (_l * dpRatio).toInt()
        val top = (_t * dpRatio).toInt()
        val bottom = (_b * dpRatio).toInt()

        val p = _view.layoutParams
        if (p is LinearLayout.LayoutParams) {
            val lp = p
            lp.setMargins(left, top, right, bottom)
            _view.layoutParams = lp
        } else if (p is RelativeLayout.LayoutParams) {
            val lp = p
            lp.setMargins(left, top, right, bottom)
            _view.layoutParams = lp
        } else if (p is TableRow.LayoutParams) {
            val lp = p
            lp.setMargins(left, top, right, bottom)
            _view.layoutParams = lp
        }
    }

    internal class c {
        var co: Context

        constructor(a: Activity) {
            co = a
        }

        constructor(a: Fragment) {
            co = a.activity!!
        }

        constructor(a: DialogFragment) {
            co = a.activity!!
        }

        fun getContext(): Context {
            return co
        }
    }

    fun _getUserReference() {
        val getUserReference = FirebaseDatabase.getInstance().getReference("skyline/users").child(this.intent.getStringExtra("uid")!!)
        getUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("banned").getValue(String::class.java) == "true") {
                        bannedUserInfo.visibility = View.VISIBLE
                        topProfileLayoutProfileImage.setImageResource(R.drawable.banned_avatar)
                        SecondUserAvatar = "null_banned"
                        topProfileLayoutStatus.setTextColor(-0x616162)
                        topProfileLayoutStatus.text = resources.getString(R.string.offline)
                    } else {
                        bannedUserInfo.visibility = View.GONE
                        if (dataSnapshot.child("avatar").getValue(String::class.java) == "null") {
                            topProfileLayoutProfileImage.setImageResource(R.drawable.avatar)
                            SecondUserAvatar = "null"
                        } else {
                            Glide.with(applicationContext).load(Uri.parse(dataSnapshot.child("avatar").getValue(String::class.java))).into(topProfileLayoutProfileImage)
                            SecondUserAvatar = dataSnapshot.child("avatar").getValue(String::class.java).toString()
                        }
                    }
                    if (dataSnapshot.child("nickname").getValue(String::class.java) == "null") {
                        topProfileLayoutUsername.text = "@" + dataSnapshot.child("username").getValue(String::class.java)
                        SecondUserName = "@" + dataSnapshot.child("username").getValue(String::class.java)
                    } else {
                        topProfileLayoutUsername.text = dataSnapshot.child("nickname").getValue(String::class.java)
                        SecondUserName = dataSnapshot.child("nickname").getValue(String::class.java).toString()
                    }
                    if (dataSnapshot.child("status").getValue(String::class.java) == "online") {
                        topProfileLayoutStatus.text = resources.getString(R.string.online)
                        topProfileLayoutStatus.setTextColor(-0xde69cb)
                    } else {
                        if (dataSnapshot.child("status").getValue(String::class.java) == "offline") {
                            topProfileLayoutStatus.text = resources.getString(R.string.offline)
                        } else {
                            _setUserLastSeen(dataSnapshot.child("status").getValue(String::class.java)!!.toDouble(), topProfileLayoutStatus)
                        }
                        topProfileLayoutStatus.setTextColor(-0x8a8b8b)
                    }
                    if (dataSnapshot.child("gender").getValue(String::class.java) == "hidden") {
                        topProfileLayoutGenderBadge.visibility = View.GONE
                    } else {
                        if (dataSnapshot.child("gender").getValue(String::class.java) == "male") {
                            topProfileLayoutGenderBadge.setImageResource(R.drawable.male_badge)
                            topProfileLayoutGenderBadge.visibility = View.VISIBLE
                        } else {
                            if (dataSnapshot.child("gender").getValue(String::class.java) == "female") {
                                topProfileLayoutGenderBadge.setImageResource(R.drawable.female_badge)
                                topProfileLayoutGenderBadge.visibility = View.VISIBLE
                            }
                        }
                    }
                    if (dataSnapshot.child("account_type").getValue(String::class.java) == "admin") {
                        topProfileLayoutVerifiedBadge.setImageResource(R.drawable.admin_badge)
                        topProfileLayoutVerifiedBadge.visibility = View.VISIBLE
                    } else {
                        if (dataSnapshot.child("account_type").getValue(String::class.java) == "moderator") {
                            topProfileLayoutVerifiedBadge.setImageResource(R.drawable.moderator_badge)
                            topProfileLayoutVerifiedBadge.visibility = View.VISIBLE
                        } else {
                            if (dataSnapshot.child("account_type").getValue(String::class.java) == "support") {
                                topProfileLayoutVerifiedBadge.setImageResource(R.drawable.support_badge)
                                topProfileLayoutVerifiedBadge.visibility = View.VISIBLE
                            } else {
                                if (dataSnapshot.child("account_premium").getValue(String::class.java) == "true") {
                                    topProfileLayoutVerifiedBadge.setImageResource(R.drawable.premium_badge)
                                    topProfileLayoutVerifiedBadge.visibility = View.VISIBLE
                                } else {
                                    if (dataSnapshot.child("verify").getValue(String::class.java) == "true") {
                                        topProfileLayoutVerifiedBadge.setImageResource(R.drawable.verified_badge)
                                        topProfileLayoutVerifiedBadge.visibility = View.VISIBLE
                                    } else {
                                        topProfileLayoutVerifiedBadge.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }
                } else {
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {

            }
        })
        val getFirstUserName = FirebaseDatabase.getInstance().getReference("skyline/users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        getFirstUserName.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("nickname").getValue(String::class.java) == "null") {
                        FirstUserName = "@" + dataSnapshot.child("username").getValue(String::class.java)
                    } else {
                        FirstUserName = dataSnapshot.child("nickname").getValue(String::class.java).toString()
                    }
                } else {

                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {

            }
        })

        _getChatMessagesRef()
    }

    fun _getChatMessagesRef() {
        val getChatsMessages = FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().currentUser!!.uid).child(this.intent.getStringExtra("uid")!!).limitToLast(ChatMessagesLimit.toInt())
        getChatsMessages.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    ChatMessagesListRecycler.visibility = View.VISIBLE
                    noChatText.visibility = View.GONE
                    ChatMessagesList.clear()

                    try {
                        val _ind = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                        for (_data in dataSnapshot.children) {
                            val _map = _data.getValue(_ind)
                            if (_map != null) {
                                ChatMessagesList.add(_map)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (ChatMessagesList.size > ChatInitialSize) {
                        ChatMessagesListRecycler.adapter!!.notifyDataSetChanged()
                        ChatMessagesListRecycler.scrollToPosition(ChatMessagesList.size - 1)
                    } else {
                        ChatMessagesListRecycler.adapter!!.notifyDataSetChanged()
                    }

                    ChatInitialSize = ChatMessagesList.size
                } else {
                    ChatMessagesListRecycler.visibility = View.GONE
                    noChatText.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {

            }
        })
    }

    fun _AudioRecorderStart() {
        cc = Calendar.getInstance()
        recordMs = 0.0
        bottomAudioRecorder.visibility = View.VISIBLE
        AudioMessageRecorder = MediaRecorder()

        val getCacheDir = externalCacheDir
        val getCacheDirName = "audio_records"
        val getCacheFolder = File(getCacheDir, getCacheDirName)
        getCacheFolder.mkdirs()
        val getRecordFile = File(getCacheFolder, cc.timeInMillis.toString() + ".mp3")
        val recordFilePath = getRecordFile.absolutePath

        AudioMessageRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        AudioMessageRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        AudioMessageRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        AudioMessageRecorder!!.setAudioEncodingBitRate(320000)
        AudioMessageRecorder!!.setOutputFile(recordFilePath)

        try {
            AudioMessageRecorder!!.prepare()
            AudioMessageRecorder!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        vbr.vibrate(48L)
        timer = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    recordMs += 500.0
                    bottomAudioRecorderTime.text = _getDurationString(recordMs.toLong())
                }
            }
        }
        _timer.scheduleAtFixedRate(timer, 0, 500)
    }

    fun _AudioRecorderStop() {
        bottomAudioRecorder.visibility = View.GONE
        if (AudioMessageRecorder != null) {
            AudioMessageRecorder!!.stop()
            AudioMessageRecorder!!.release()
            AudioMessageRecorder = null
        }
        vbr.vibrate(48L)
        timer!!.cancel()
    }

    fun _getDurationString( _durationInMillis: Long): String {
        var seconds = _durationInMillis / 1000
        var minutes = seconds / 60
        val hours = minutes / 60
        seconds %= 60
        minutes %= 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun _getOldChatMessagesRef() {
        ChatMessagesLimit += 80.0
        run {
            val mExecutorService: ExecutorService = Executors.newSingleThreadExecutor()
            val mMainHandler = Handler(Looper.getMainLooper())

            mExecutorService.execute {
                val getChatsMessages = FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().currentUser!!.uid).child(this@ChatActivity.intent.getStringExtra("uid")!!).limitToLast(ChatMessagesLimit.toInt())
                getChatsMessages.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                        mMainHandler.post {
                            if (dataSnapshot.exists()) {
                                ChatMessagesListRecycler.visibility = View.VISIBLE
                                noChatText.visibility = View.GONE
                                ChatMessagesList.clear()
                                try {
                                    val _ind = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                                    for (_data in dataSnapshot.children) {
                                        val _map = _data.getValue(_ind)
                                        if (_map != null) {
                                            ChatMessagesList.add(_map)
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                ChatMessagesListRecycler.adapter!!.notifyDataSetChanged()
                            } else {
                                ChatMessagesListRecycler.visibility = View.GONE
                                noChatText.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onCancelled(@NonNull databaseError: DatabaseError) {

                    }
                })
            }
        }
    }

    fun _DeleteMessageDialog( _data: ArrayList<HashMap<String, Any>>, _position: Double) {
        val zorry = MaterialAlertDialogBuilder(this@ChatActivity)

        zorry.setTitle("Delete")
        zorry.setMessage("Are you sure you want to delete this message. Please confirm your decision.")
        zorry.setIcon(R.drawable.popup_ic_3)
        zorry.setPositiveButton("Delete") { _dialog, _which ->
            FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().currentUser!!.uid).child(this@ChatActivity.intent.getStringExtra("uid")!!).child(_data[_position.toInt()]["key"].toString()).removeValue()
            FirebaseDatabase.getInstance().getReference("skyline/chats").child(this@ChatActivity.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child(_data[_position.toInt()]["key"].toString()).removeValue()
        }
        zorry.setNegativeButton("No") { _dialog, _which ->

        }
        zorry.create().show()
    }

    fun _ScrollingText( _view: TextView) {
        _view.setSingleLine(true)
        _view.ellipsize = TextUtils.TruncateAt.MARQUEE
        _view.isSelected = true
    }

    fun _setUserLastSeen( _currentTime: Double, _txt: TextView) {
        val c1 = Calendar.getInstance()
        val c2 = Calendar.getInstance()
        c2.timeInMillis = _currentTime.toLong()

        val time_diff = c1.timeInMillis - c2.timeInMillis

        var seconds = time_diff / 1000
        var minutes = seconds / 60
        var hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        if (seconds < 60) {
            if (seconds < 2) {
                _txt.text = "1 " + resources.getString(R.string.status_text_seconds)
            } else {
                _txt.text = seconds.toString() + " " + resources.getString(R.string.status_text_seconds)
            }
        } else if (minutes < 60) {
            if (minutes < 2) {
                _txt.text = "1 " + resources.getString(R.string.status_text_minutes)
            } else {
                _txt.text = minutes.toString() + " " + resources.getString(R.string.status_text_minutes)
            }
        } else if (hours < 24) {
            if (hours < 2) {
                _txt.text = "1 " + resources.getString(R.string.status_text_hours)
            } else {
                _txt.text = hours.toString() + " " + resources.getString(R.string.status_text_hours)
            }
        } else if (days < 7) {
            if (days < 2) {
                _txt.text = "1 " + resources.getString(R.string.status_text_days)
            } else {
                _txt.text = days.toString() + " " + resources.getString(R.string.status_text_days)
            }
        } else if (weeks < 4) {
            if (weeks < 2) {
                _txt.text = "1 " + resources.getString(R.string.status_text_week)
            } else {
                _txt.text = weeks.toString() + " " + resources.getString(R.string.status_text_week)
            }
        } else if (months < 12) {
            if (months < 2) {
                _txt.text = "1 " + resources.getString(R.string.status_text_month)
            } else {
                _txt.text = months.toString() + " " + resources.getString(R.string.status_text_month)
            }
        } else {
            if (years < 2) {
                _txt.text = "1 " + resources.getString(R.string.status_text_years)
            } else {
                _txt.text = years.toString() + " " + resources.getString(R.string.status_text_years)
            }
        }
    }

    fun _textview_mh( _txt: TextView, _value: String) {
        _txt.movementMethod = LinkMovementMethod.getInstance()
        updateSpan(_value, _txt)
    }

    private fun updateSpan(str: String, _txt: TextView) {
        val ssb = SpannableStringBuilder(str)
        val pattern = Pattern.compile("(?<![^\\s])(([@]{1}|[#]{1})([A-Za-z0-9_-]\\.?)+)(?![^\\s,])|\\*\\*(.*?)\\*\\*|__(.*?)__|~~(.*?)~~|_(.*?)_|\\*(.*?)\\*|///(.*?)///")
        val matcher = pattern.matcher(str)
        var offset = 0

        while (matcher.find()) {
            val start = matcher.start() + offset
            val end = matcher.end() + offset

            if (matcher.group(3) != null) {
                val span = ProfileSpan()
                ssb.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else if (matcher.group(4) != null) {
                val boldText = matcher.group(4)
                ssb.replace(start, end, boldText)
                ssb.setSpan(StyleSpan(Typeface.BOLD), start, start + boldText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                offset -= 4
            } else if (matcher.group(5) != null) {
                val italicText = matcher.group(5)
                ssb.replace(start, end, italicText)
                ssb.setSpan(StyleSpan(Typeface.ITALIC), start, start + italicText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                offset -= 4
            } else if (matcher.group(6) != null) {
                val strikethroughText = matcher.group(6)
                ssb.replace(start, end, strikethroughText)
                ssb.setSpan(StrikethroughSpan(), start, start + strikethroughText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                offset -= 4
            } else if (matcher.group(7) != null) {
                val underlineText = matcher.group(7)
                ssb.replace(start, end, underlineText)
                ssb.setSpan(UnderlineSpan(), start, start + underlineText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                offset -= 2
            } else if (matcher.group(8) != null) {
                val italicText = matcher.group(8)
                ssb.replace(start, end, italicText)
                ssb.setSpan(StyleSpan(Typeface.ITALIC), start, start + italicText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                offset -= 2
            } else if (matcher.group(9) != null) {
                val boldItalicText = matcher.group(9)
                ssb.replace(start, end, boldItalicText)
                ssb.setSpan(StyleSpan(Typeface.BOLD_ITALIC), start, start + boldItalicText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                offset -= 6
            }
        }
        _txt.text = ssb
    }

    private inner class ProfileSpan : ClickableSpan() {

        override fun onClick(view: View) {
            if (view is TextView) {
                val tv = view
                if (tv.text is Spannable) {
                    val sp = tv.text as Spannable
                    val start = sp.getSpanStart(this)
                    val end = sp.getSpanEnd(this)
                    object_clicked = sp.subSequence(start, end).toString()
                    handle = object_clicked.replace("@", "")
                    val getReference = FirebaseDatabase.getInstance().getReference()
                        .child("synapse/username")
                        .child(handle)
                    getReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.child("uid").getValue(String::class.java) != "null") {
                                    i.setClass(applicationContext, ProfileActivity::class.java)
                                    i.putExtra("uid", dataSnapshot.child("uid").getValue(String::class.java))
                                    startActivity(i)
                                } else {

                                }
                            } else {
                            }
                        }

                        override fun onCancelled(@NonNull databaseError: DatabaseError) {

                        }
                    })
                }
            }
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
            ds.color = Color.parseColor("#FFFF00")
            ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    }

    fun _swipe2rply( _view: View, _position: Double, _data: ArrayList<HashMap<String, Any>>) {
        ReplyMessageID = _data[_position.toInt()]["key"].toString()
        if (_data[_position.toInt()]["uid"].toString() == FirebaseAuth.getInstance().currentUser!!.uid) {
            mMessageReplyLayoutBodyRightUsername.text = FirstUserName
        } else {
            mMessageReplyLayoutBodyRightUsername.text = SecondUserName
        }
        mMessageReplyLayoutBodyRightMessage.text = _data[_position.toInt()]["message_text"].toString()
        mMessageReplyLayout.visibility = View.VISIBLE
        vbr.vibrate(48L)
    }

    fun _send_btn() {
        val messageText = message_et.text.toString().trim()

        if (selectedImagesPaths.isEmpty() && messageText.isEmpty()) {
            SketchwareUtil.showMessage(applicationContext, "Message or image cannot be empty.")
            return
        }

        _LoadingDialog(true)
        overall_upload_prog.visibility = View.VISIBLE
        overall_upload_prog.progress = 0

        if (selectedImagesPaths.isNotEmpty()) {
            val uploadTasks = ArrayList<Task<Uri>>()
            val uploadedImageUrls = ArrayList<String>()

            selectedImagesPaths.forEachIndexed { index, imagePath ->
                (selected_images_recycler.adapter as? SelectedImagesAdapter)?.updateProgress(imagePath, 0)

                val uploadTask = ImageUploader.uploadImageWithProgress(imagePath,
                    object : ImageUploader.UploadProgressListener {
                        override fun onProgress(filePath: String, progress: Int) {
                            uploadProgressMap[filePath] = progress
                            (selected_images_recycler.adapter as? SelectedImagesAdapter)?.updateProgress(filePath, progress)
                            var totalProgress = 0
                            uploadProgressMap.values.forEach { totalProgress += it }
                            overall_upload_prog.progress = totalProgress / selectedImagesPaths.size
                        }
                    })

                uploadTask.addOnSuccessListener { uri ->
                    uploadedImageUrls.add(uri.toString())
                }.addOnFailureListener { e ->
                    SketchwareUtil.showMessage(applicationContext, "Failed to upload image: ${e.message}")
                }
                uploadTasks.add(uploadTask)
            }

            Tasks.whenAllComplete(uploadTasks)
                .addOnCompleteListener { allTasks ->
                    _LoadingDialog(false)
                    overall_upload_prog.visibility = View.GONE
                    if (allTasks.isSuccessful) {
                        _sendChatMessage(messageText, uploadedImageUrls)
                    } else {
                        SketchwareUtil.showMessage(applicationContext, "Some image uploads failed.")
                        selectedImagesPaths.clear()
                        uploadProgressMap.clear()
                        updateSelectedImagesUI()
                    }
                }
        } else {
            _sendChatMessage(messageText, emptyList())
        }
    }

    private fun _sendChatMessage(messageText: String, imageUrls: List<String>) {
        cc = Calendar.getInstance()
        val uniqueMessageKey = main.push().key

        ChatSendMap = HashMap()
        ChatSendMap["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
        ChatSendMap["TYPE"] = "MESSAGE"
        ChatSendMap["message_text"] = messageText
        if (imageUrls.isNotEmpty()) {
            ChatSendMap["message_image_uris"] = imageUrls.joinToString(",")
        }
        ChatSendMap["message_state"] = "sended"
        if (ReplyMessageID != "null") {
            ChatSendMap["replied_message_id"] = ReplyMessageID
        }
        ChatSendMap["key"] = uniqueMessageKey!!
        ChatSendMap["push_date"] = cc.timeInMillis.toString()

        FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().currentUser!!.uid).child(this.intent.getStringExtra("uid")!!).child(uniqueMessageKey).updateChildren(ChatSendMap)
        FirebaseDatabase.getInstance().getReference("skyline/chats").child(this.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child(uniqueMessageKey).updateChildren(ChatSendMap)

        ChatInboxSend = HashMap()
        ChatInboxSend["uid"] = this.intent.getStringExtra("uid")!!
        ChatInboxSend["TYPE"] = "MESSAGE"
        ChatInboxSend["last_message_uid"] = FirebaseAuth.getInstance().currentUser!!.uid
        ChatInboxSend["last_message_text"] = if (messageText.isNotEmpty()) messageText else "Sent image(s)"
        ChatInboxSend["last_message_state"] = "sended"
        ChatInboxSend["push_date"] = cc.timeInMillis.toString()
        FirebaseDatabase.getInstance().getReference("skyline/inbox").child(FirebaseAuth.getInstance().currentUser!!.uid).child(this.intent.getStringExtra("uid")!!).updateChildren(ChatInboxSend)

        ChatInboxSend2 = HashMap()
        ChatInboxSend2["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
        ChatInboxSend2["TYPE"] = "MESSAGE"
        ChatInboxSend2["last_message_uid"] = FirebaseAuth.getInstance().currentUser!!.uid
        ChatInboxSend2["last_message_text"] = if (messageText.isNotEmpty()) messageText else "Sent image(s)"
        ChatInboxSend2["last_message_state"] = "sended"
        ChatInboxSend2["push_date"] = cc.timeInMillis.toString()
        FirebaseDatabase.getInstance().getReference("skyline/inbox").child(this.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(ChatInboxSend2)

        message_et.setText("")
        selectedImagesPaths.clear()
        uploadProgressMap.clear()
        updateSelectedImagesUI()

        FirebaseDatabase.getInstance().getReference("skyline/chats").child(this.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child("typing-message").removeValue()
        ChatSendMap.clear()
        ChatInboxSend.clear()
        ChatInboxSend2.clear()
        ReplyMessageID = "null"
        mMessageReplyLayout.visibility = View.GONE
    }

    fun _Block( _uid: String) {
        block = HashMap()
        block[_uid] = "true"
        blocklist.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(block)
        block.clear()
    }

    fun _TransitionManager( _view: View, _duration: Double) {
        val viewgroup = _view as ViewGroup
        val autoTransition = android.transition.AutoTransition()
        autoTransition.duration = _duration.toLong()
        android.transition.TransitionManager.beginDelayedTransition(viewgroup, autoTransition)
    }

    fun _Unblock_this_user() {
        val blocklistRef = FirebaseDatabase.getInstance().getReference("skyline/blocklist")
        val myUid = FirebaseAuth.getInstance().currentUser!!.uid
        val uidToRemove = this.intent.getStringExtra("uid")

        blocklistRef.child(myUid).child(uidToRemove!!).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _getUserReference()
                message_input_overall_container.visibility = View.VISIBLE
                unblock_btn.visibility = View.GONE
                blocked_info_card.visibility = View.GONE
                SketchwareUtil.showMessage(applicationContext, "User unblocked.")
            } else {
                SketchwareUtil.showMessage(applicationContext, "Failed to unblock user: ${task.exception?.message}")
            }
        }
    }

    fun _LoadingDialog( _visibility: Boolean) {
        if (_visibility) {
            if (SynapseLoadingDialog == null) {
                SynapseLoadingDialog = ProgressDialog(this)
                SynapseLoadingDialog!!.setCancelable(false)
                SynapseLoadingDialog!!.setCanceledOnTouchOutside(false)

                SynapseLoadingDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                SynapseLoadingDialog!!.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            }
            SynapseLoadingDialog!!.show()
            SynapseLoadingDialog!!.setContentView(R.layout.loading_synapse)

            val loading_bar_layout = SynapseLoadingDialog!!.findViewById<LinearLayout>(R.id.loading_bar_layout)
        } else {
            if (SynapseLoadingDialog != null) {
                SynapseLoadingDialog!!.dismiss()
            }
        }
    }

    fun _ImgRound( _imageview: ImageView, _value: Double) {
        val gd = GradientDrawable()
        gd.setColor(android.R.color.transparent)
        gd.setCornerRadius(_value.toFloat())
        _imageview.clipToOutline = true
        _imageview.background = gd
    }

    fun _OpenWebView( _URL: String) {
        AndroidDevelopersBlogURL = _URL
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(Color.parseColor("#242D39"))
        val customtabsintent = builder.build()
        customtabsintent.launchUrl(this, Uri.parse(AndroidDevelopersBlogURL))
    }

    inner class ChatMessagesListRecyclerAdapter(private val _data: ArrayList<HashMap<String, Any>>) : RecyclerView.Adapter<ChatMessagesListRecyclerAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val _inflater = layoutInflater
            val _v = _inflater.inflate(R.layout.chat_msg_cv_synapse, parent, false)
            return ViewHolder(_v)
        }

        override fun onBindViewHolder( _holder: ViewHolder, _position: Int) {
            val _view = _holder.itemView

            val showOldMessagesProgress = _view.findViewById<ProgressBar>(R.id.showOldMessagesProgress)
            val body = _view.findViewById<LinearLayout>(R.id.body)
            val mProfileCard = _view.findViewById<CardView>(R.id.mProfileCard)
            val message_layout = _view.findViewById<LinearLayout>(R.id.message_layout)
            val mProfileImage = _view.findViewById<ImageView>(R.id.mProfileImage)
            val menuView_d = _view.findViewById<LinearLayout>(R.id.menuView_d)
            val messageBG = _view.findViewById<LinearLayout>(R.id.messageBG)
            val my_message_info = _view.findViewById<LinearLayout>(R.id.my_message_info)
            val mRepliedMessageLayout = _view.findViewById<LinearLayout>(R.id.mRepliedMessageLayout)
            val mMessageImageBody = _view.findViewById<CardView>(R.id.mMessageImageBody)
            val typingStatusIcon = _view.findViewById<ImageView>(R.id.typingStatusIcon)
            val lottie1 = _view.findViewById<com.airbnb.lottie.LottieAnimationView>(R.id.lottie1)
            val message_text = _view.findViewById<TextView>(R.id.message_text)
            val mRepliedMessageLayoutLeftBar = _view.findViewById<LinearLayout>(R.id.mRepliedMessageLayoutLeftBar)
            val mRepliedMessageLayoutRightBody = _view.findViewById<LinearLayout>(R.id.mRepliedMessageLayoutRightBody)
            val mRepliedMessageLayoutUsername = _view.findViewById<TextView>(R.id.mRepliedMessageLayoutUsername)
            val mRepliedMessageLayoutMessage = _view.findViewById<TextView>(R.id.mRepliedMessageLayoutMessage)
            val mMessageImageView = _view.findViewById<ImageView>(R.id.mMessageImageView)
            val date = _view.findViewById<TextView>(R.id.date)
            val message_state = _view.findViewById<ImageView>(R.id.message_state)

            typingStatusIcon.visibility = View.GONE
            lottie1.visibility = View.GONE
            val push = Calendar.getInstance()
            mProfileCard.background = GradientDrawable().apply { setCornerRadius(300f); setColor(Color.TRANSPARENT) }
            _ImageColor(typingStatusIcon, -0x424243)
            if (_data[_position].containsKey("typingMessageStatus")) {
                body.translationY = 10f
                run {
                    val SketchUi = GradientDrawable()
                    val d = applicationContext.resources.displayMetrics.density.toInt()
                    SketchUi.setColor(-0x1)
                    SketchUi.setCornerRadius((d * 25).toFloat())
                    messageBG.background = SketchUi
                }
                _setMargin(message_layout, 60.0, 0.0, 4.0, 4.0)
                message_layout.gravity = Gravity.CENTER or Gravity.LEFT
                body.gravity = Gravity.TOP or Gravity.LEFT
                message_state.visibility = View.GONE
                mProfileCard.visibility = View.GONE
                if (SecondUserAvatar == "null_banned") {
                    mProfileImage.setImageResource(R.drawable.banned_avatar)
                } else {
                    if (SecondUserAvatar == "null") {
                        mProfileImage.setImageResource(R.drawable.avatar)
                    } else {
                        Glide.with(applicationContext).load(Uri.parse(SecondUserAvatar)).into(mProfileImage)
                    }
                }
                showOldMessagesProgress.visibility = View.GONE
                my_message_info.visibility = View.GONE
                message_text.visibility = View.GONE
                mMessageImageBody.visibility = View.GONE
                mRepliedMessageLayout.visibility = View.GONE
                lottie1.visibility = View.VISIBLE
                typingStatusIcon.visibility = View.GONE
            } else {
                body.translationY = 0f
                if (_data[_position]["uid"].toString() == FirebaseAuth.getInstance().currentUser!!.uid) {
                    body.gravity = Gravity.TOP or Gravity.RIGHT
                    message_layout.gravity = Gravity.CENTER or Gravity.RIGHT
                    _setMargin(message_layout, 0.0, 60.0, 0.0, 0.0)
                    message_text.setTextColor(-0x1)
                    message_state.visibility = View.VISIBLE
                    mProfileCard.visibility = View.GONE
                    mRepliedMessageLayoutLeftBar.background = GradientDrawable().apply { setCornerRadius(300f); setColor(-0x94b401) }
                    mRepliedMessageLayoutUsername.setTextColor(-0x111112)
                    mRepliedMessageLayoutMessage.setTextColor(-0x111112)
                    try {
                        var cornerRadius = 0
                        try {
                            cornerRadius = appSettings.getString("ChatCornerRadius", "")!!.toInt()
                        } catch (e: Exception) {
                            try {
                                cornerRadius = appSettings.getString("ChatCornerRadius", "")!!.toDouble().toInt()
                            } catch (ex: Exception) {
                                cornerRadius = 27
                            }
                        }

                        val SketchUi = GradientDrawable()
                        val d = applicationContext.resources.displayMetrics.density.toInt()
                        SketchUi.setColor(-0x94b401)
                        SketchUi.setCornerRadius((d * cornerRadius).toFloat())
                        val SketchUiRD = RippleDrawable(
                            ColorStateList(arrayOf(intArrayOf()), intArrayOf(-0x1f1f20)), SketchUi, null
                        )

                        messageBG.background = SketchUiRD
                        messageBG.isClickable = true
                    } catch (e: Exception) {
                        run {
                            val SketchUi = GradientDrawable()
                            val d = applicationContext.resources.displayMetrics.density.toInt()
                            SketchUi.setColor(-0x94b401)
                            SketchUi.setCornerRadius((d * 27).toFloat())
                            val SketchUiRD = RippleDrawable(ColorStateList(arrayOf(intArrayOf()), intArrayOf(-0x1f1f20)), SketchUi, null)
                            messageBG.background = SketchUiRD
                            messageBG.isClickable = true
                        }
                    }
                } else {
                    body.gravity = Gravity.TOP or Gravity.LEFT
                    message_layout.gravity = Gravity.CENTER or Gravity.LEFT
                    _setMargin(message_layout, 60.0, 0.0, 4.0, 0.0)
                    message_text.setTextColor(-0x1000000)
                    message_state.visibility = View.GONE
                    mProfileCard.visibility = View.GONE
                    mRepliedMessageLayoutLeftBar.background = GradientDrawable().apply { setCornerRadius(300f); setColor(resources.getColor(R.color.colorPrimary)) }
                    mRepliedMessageLayoutUsername.setTextColor(resources.getColor(R.color.colorPrimary))
                    mRepliedMessageLayoutMessage.setTextColor(-0x1000000)
                    if (SecondUserAvatar == "null") {
                        mProfileImage.setImageResource(R.drawable.avatar)
                    } else {
                        Glide.with(applicationContext).load(Uri.parse(SecondUserAvatar)).into(mProfileImage)
                    }
                    try {
                        var cornerRadius = 0
                        try {
                            cornerRadius = appSettings.getString("ChatCornerRadius", "")!!.toInt()
                        } catch (e: Exception) {
                            try {
                                cornerRadius = appSettings.getString("ChatCornerRadius", "")!!.toDouble().toInt()
                            } catch (ex: Exception) {
                                cornerRadius = 27
                            }
                        }

                        val SketchUi = GradientDrawable()
                        val d = applicationContext.resources.displayMetrics.density.toInt()
                        SketchUi.setColor(-0x1)
                        SketchUi.setCornerRadius((d * cornerRadius).toFloat())
                        SketchUi.setStroke(d * 2, -0x202021)
                        val SketchUiRD = RippleDrawable(
                            ColorStateList(arrayOf(intArrayOf()), intArrayOf(-0x1f1f20)), SketchUi, null
                        )

                        messageBG.background = SketchUiRD
                        messageBG.isClickable = true
                    } catch (e: Exception) {
                        run {
                            val SketchUi = GradientDrawable()
                            val d = applicationContext.resources.displayMetrics.density.toInt()
                            SketchUi.setColor(-0x1)
                            SketchUi.setCornerRadius((d * 27).toFloat())
                            SketchUi.setStroke(d * 2, -0x202021)
                            val SketchUiRD = RippleDrawable(ColorStateList(arrayOf(intArrayOf()), intArrayOf(-0x1f1f20)), SketchUi, null)
                            messageBG.background = SketchUiRD
                            messageBG.isClickable = true
                        }
                    }
                }
                TextStylingUtil(message_text.context).applyStyling(_data[_position]["message_text"].toString(), message_text)
                push.timeInMillis = _data[_position]["push_date"].toString().toDouble().toLong()
                date.text = SimpleDateFormat("hh:mm a").format(push.time)
                if (_data[_position].containsKey("replied_message_id")) {
                    run {
                        val mExecutorService: ExecutorService = Executors.newSingleThreadExecutor()
                        val mMainHandler = Handler(Looper.getMainLooper())

                        mExecutorService.execute {
                            val getRepliedMessageRef = FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().currentUser!!.uid).child(this@ChatActivity.intent.getStringExtra("uid")!!).child(_data[_position]["replied_message_id"].toString())
                            getRepliedMessageRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                                    mMainHandler.post {
                                        if (dataSnapshot.exists()) {
                                            if (dataSnapshot.child("uid").getValue(String::class.java) == FirebaseAuth.getInstance().currentUser!!.uid) {
                                                mRepliedMessageLayoutUsername.text = FirstUserName
                                            } else {
                                                mRepliedMessageLayoutUsername.text = SecondUserName
                                            }
                                            if (dataSnapshot.child("message_text").getValue(String::class.java) != null) {
                                                mRepliedMessageLayoutMessage.text = dataSnapshot.child("message_text").getValue(String::class.java)
                                                mRepliedMessageLayout.visibility = View.VISIBLE
                                            } else {
                                                mRepliedMessageLayout.visibility = View.GONE
                                            }
                                        } else {
                                            mRepliedMessageLayout.visibility = View.GONE
                                        }
                                    }
                                }

                                override fun onCancelled(@NonNull databaseError: DatabaseError) {

                                }
                            })
                        }
                    }

                    mRepliedMessageLayout.visibility = View.VISIBLE
                } else {
                    mRepliedMessageLayout.visibility = View.GONE
                }
                if (_data[_position].containsKey("message_image_uris")) {
                    val imageUrlsString = _data[_position]["message_image_uris"].toString()
                    val imageUrls = imageUrlsString.split(",").map { it.trim() }

                    if (imageUrls.isNotEmpty()) {
                        Glide.with(applicationContext).load(Uri.parse(imageUrls[0])).into(mMessageImageView)
                        _setMargin(message_text, 0.0, 0.0, 8.0, 0.0)
                        mMessageImageBody.visibility = View.VISIBLE
                        mMessageImageView.setOnClickListener {
                            _OpenWebView(imageUrls[0])
                        }
                    } else {
                        _setMargin(message_text, 0.0, 0.0, 0.0, 0.0)
                        mMessageImageBody.visibility = View.GONE
                    }
                } else {
                    _setMargin(message_text, 0.0, 0.0, 0.0, 0.0)
                    mMessageImageBody.visibility = View.GONE
                }
                if (_data[_position]["message_state"].toString() == "sended") {
                    message_state.setImageResource(R.drawable.icon_done_round)
                    _ImageColor(message_state, -0x3bd3bc)
                } else {
                    if (_data[_position]["message_state"].toString() == "seen") {
                        message_state.setImageResource(R.drawable.icon_done_all_round)
                        _ImageColor(message_state, resources.getColor(R.color.colorPrimary))
                    }
                }
                if (_data.size > 79) {
                    if (_position == 0) {
                        showOldMessagesProgress.visibility = View.VISIBLE
                        loadTimer = object : TimerTask() {
                            override fun run() {
                                runOnUiThread {
                                    _getOldChatMessagesRef()
                                    showOldMessagesProgress.visibility = View.GONE
                                }
                            }
                        }
                        _timer.schedule(loadTimer, 1000)
                    } else {
                        showOldMessagesProgress.visibility = View.GONE
                    }
                } else {
                    showOldMessagesProgress.visibility = View.GONE
                }
                if (_position == 0) {
                    my_message_info.visibility = View.GONE
                    mProfileImage.visibility = View.GONE
                    if (_data.size == 1) {
                        my_message_info.visibility = View.VISIBLE
                        mProfileImage.visibility = View.VISIBLE
                    } else {
                        if (_data[_position]["uid"].toString() != _data[_position + 1]["uid"].toString()) {
                            my_message_info.visibility = View.VISIBLE
                            mProfileImage.visibility = View.VISIBLE
                        }
                    }
                } else {
                    if (_data[_position]["uid"].toString() != _data[_position - 1]["uid"].toString()) {
                        my_message_info.visibility = View.GONE
                        mProfileImage.visibility = View.GONE
                        if (_position == (_data.size - 1)) {
                            my_message_info.visibility = View.VISIBLE
                            mProfileImage.visibility = View.VISIBLE
                        } else {
                            if (_data[_position]["uid"].toString() != _data[_position + 1]["uid"].toString()) {
                                my_message_info.visibility = View.VISIBLE
                                mProfileImage.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        if (_position == (_data.size - 1)) {
                            my_message_info.visibility = View.VISIBLE
                            mProfileImage.visibility = View.VISIBLE
                        } else {
                            if (_data[_position]["uid"].toString() == _data[_position - 1]["uid"].toString() && _data[_position]["uid"].toString() == _data[_position + 1]["uid"].toString()) {
                                my_message_info.visibility = View.GONE
                                mProfileImage.visibility = View.GONE
                            } else {
                                if (_data[_position]["uid"].toString() == _data[_position + 1]["uid"].toString()) {
                                    my_message_info.visibility = View.GONE
                                    mProfileImage.visibility = View.GONE
                                } else {
                                    my_message_info.visibility = View.VISIBLE
                                    mProfileImage.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
                if (_data[_position]["uid"].toString() != FirebaseAuth.getInstance().currentUser!!.uid) {
                    if (_data[_position]["message_state"].toString() == "sended") {
                        FirebaseDatabase.getInstance().getReference("skyline/chats").child(this@ChatActivity.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child(_data[_position]["key"].toString()).child("message_state").setValue("seen")
                        FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().currentUser!!.uid).child(this@ChatActivity.intent.getStringExtra("uid")!!).child(_data[_position]["key"].toString()).child("message_state").setValue("seen")
                        FirebaseDatabase.getInstance().getReference("skyline/inbox").child(this@ChatActivity.intent.getStringExtra("uid")!!).child(FirebaseAuth.getInstance().currentUser!!.uid).child("last_message_state").setValue("seen")
                    }
                }
                lottie1.visibility = View.GONE
                message_text.visibility = View.VISIBLE
            }
            message_layout.setOnLongClickListener {
                _messageOverviewPopup(menuView_d, _position.toDouble(), _data)
                true
            }
            messageBG.setOnLongClickListener {
                _messageOverviewPopup(menuView_d, _position.toDouble(), _data)
                true
            }
            message_text.setOnLongClickListener {
                _messageOverviewPopup(menuView_d, _position.toDouble(), _data)
                true
            }
            mProfileImage.setOnClickListener {
                i.setClass(applicationContext, ProfileActivity::class.java)
                i.putExtra("uid", this@ChatActivity.intent.getStringExtra("uid"))
                startActivity(i)
            }
            try {
                try {
                    message_text.textSize = appSettings.getString("ChatTextSize", "")!!.toInt().toFloat()
                    mRepliedMessageLayoutMessage.textSize = appSettings.getString("ChatTextSize", "")!!.toInt().toFloat()
                } catch (e: Exception) {
                    try {
                        message_text.textSize = appSettings.getString("ChatTextSize", "")!!.toDouble().toFloat()
                        mRepliedMessageLayoutMessage.textSize = appSettings.getString("ChatTextSize", "")!!.toDouble().toFloat()
                    } catch (ex: Exception) {
                        message_text.textSize = 16f
                    }
                }
            } catch (e: Exception) {

            }
        }

        override fun getItemCount(): Int {
            return _data.size
        }

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
    }

    inner class SelectedImagesAdapter(
        private val selectedPaths: ArrayList<String>,
        private val progressMap: HashMap<String, Int>
    ) : RecyclerView.Adapter<SelectedImagesAdapter.SelectedImageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_selected_image, parent, false)
            return SelectedImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
            val imagePath = selectedPaths[position]
            Glide.with(holder.itemView.context).load(File(imagePath)).into(holder.imageView)
            holder.removeButton.setOnClickListener {
                val removedPath = selectedPaths.removeAt(position)
                progressMap.remove(removedPath)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, selectedPaths.size)
                updateSelectedImagesUI()
            }

            val progress = progressMap[imagePath] ?: 0
            if (progress > 0 && progress < 100) {
                holder.progressBar.progress = progress
                holder.progressBar.visibility = View.VISIBLE
            } else {
                holder.progressBar.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int = selectedPaths.size

        fun updateProgress(filePath: String, progress: Int) {
            val index = selectedPaths.indexOf(filePath)
            if (index != -1) {
                progressMap[filePath] = progress
                notifyItemChanged(index)
            }
        }

        inner class SelectedImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.selected_img_preview)
            val removeButton: ImageView = itemView.findViewById(R.id.remove_selected_img_icon)
            val progressBar: CircularProgressIndicator = itemView.findViewById(R.id.image_upload_progress)
        }
    }
}