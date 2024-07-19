package com.example.chatbot

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    @SuppressLint("SecretInSource")
    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = Constant.MODEL_NAME,
        apiKey = Constant.API_KEY

    )

    fun sendMessage(question: String) {
        viewModelScope.launch {

            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) {
                            text(it.message)
                        }
                    }.toList()
                )
                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Typing...", "model"))
                val response = chat.sendMessage(question)
                messageList.removeLast()
                messageList.add(MessageModel(response.text.toString(), "model"))
            }catch (e:Exception){
                messageList.removeLast()
                messageList.add(MessageModel("Error: ${e.message}", "model"))
            }


        }
    }
}