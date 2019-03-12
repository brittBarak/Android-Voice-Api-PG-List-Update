package com.nexmo.example.britt.playingvapi

import android.arch.lifecycle.*
import android.os.Bundle
import android.view.View
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_talk.view.*
import android.widget.ArrayAdapter


class MainActivity : AppCompatActivity(), View.OnClickListener, Observer<ListHolder<NccoAction>> {

    override fun onChanged(value: ListHolder<NccoAction>?) {
        list.adapter?.let {
            value?.applyChange(it)
        }
    }

    lateinit var uiModel: CallRequestUiModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uiModel = ViewModelProviders.of(this).get(CallRequestUiModel::class.java)
        lifecycle.addObserver(TextWatchersObserver(uiModel, tilToNum, tilFromNum))


        list.adapter = NccoAdapter(uiModel.actions)
        list.layoutManager = LinearLayoutManager(this)

        fabMakeCall.setOnClickListener(this)

        btnAddTalk.setOnClickListener(this)
        btnAddStream.setOnClickListener(this)
        btnAddInput.setOnClickListener(this)


        uiModel.actions.observe(this, this)
    }

    override fun onClick(v: View?) {
        when (v) {
            fabMakeCall -> executeMakeCall(baseContext, uiModel)
            btnAddTalk -> addTalkAction()
            btnAddStream -> addStreamAction()
            btnAddInput -> addInputAction()
        }
    }

    fun addTalkAction() {
        AlertDialog.Builder(this).apply {
            val v = layoutInflater.inflate(R.layout.dialog_talk, null)
            val onClickListener: (DialogInterface, Int) -> Unit = { _, _ ->
                val talkAction = Talk(
                    text = v.etText.text.toString(),
                    voiceName = v.spVoices.selectedItem.toString()
                )

                addAction(talkAction)
            }

            setView(v)
            setPositiveButton(android.R.string.ok, onClickListener)
            setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }

            show()
        }
    }

    fun addStreamAction() {
        var tuneNames: List<String> = Tunes.values().map { it.name }
        // Create SimpleAdapter object.
        val dialogAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, tuneNames)

        AlertDialog.Builder(this).apply {
            setTitle("Select audio stream")
            setAdapter(dialogAdapter) { _, which ->
                val selectedTune = Tunes.values()[which]
                addAction(Stream(listOf(selectedTune.url), selectedTune.name))
            }
            setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }

            show()
        }
    }


    fun addInputAction() {
        addAction(Input())
    }

    fun addAction(action: NccoAction) {
        uiModel.actions.addItem(action)
    }


    class CallRequestUiModel : ViewModel() {
        lateinit var toPhone: String
        lateinit var fromPhone: String
        val actions = ListLiveData<NccoAction>()
    }


}