package com.example.speechapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.speechapp.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var speechRecognizer: SpeechRecognizer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val granted = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireActivity().applicationContext)
        speechRecognizer?.setRecognitionListener(createRecognitionListenerStringStream { Log.i("sakai", it) })

        binding.startChatButton.setOnClickListener {
            speechRecognizer?.startListening(createSpeechIntent())
        }
        binding.endChatButton.setOnClickListener {
            speechRecognizer?.stopListening()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer?.destroy()
        _binding = null
    }

    private fun createSpeechIntent() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        // ??????Extra??????????????????https://developer.android.com/reference/android/speech/RecognizerIntent#ACTION_RECOGNIZE_SPEECH
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        // ????????????????????????????????????
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
    }

    /** ?????????????????????????????? TextView ??????????????????????????????????????????*/
    private fun createRecognitionListenerStringStream(onResult : (String)-> Unit) : RecognitionListener {
        return object : RecognitionListener {
            override fun onRmsChanged(rmsdB: Float) { /** ?????????????????????????????? */ }
            override fun onReadyForSpeech(params: Bundle) { onResult("onReadyForSpeech") }
            override fun onBufferReceived(buffer: ByteArray) { onResult("onBufferReceived") }
            override fun onPartialResults(partialResults: Bundle) {
                val stringArray = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                onResult("onPartialResults" + stringArray.toString())
            }
            override fun onEvent(eventType: Int, params: Bundle) { onResult("onEvent") }
            override fun onBeginningOfSpeech() { onResult("onBeginningOfSpeech") }
            override fun onEndOfSpeech() { onResult("onEndOfSpeech") }
            override fun onError(error: Int) {
                // RecognizerIntent????????????????????????Extra?????????????????????????????????????????????????????????????????????
                speechRecognizer?.startListening(createSpeechIntent())
                onResult("onError")
            }
            override fun onResults(results: Bundle) {
                val stringArray = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                onResult("onResults " + stringArray.toString())
            }
        }
    }

    companion object {
        private const val PERMISSIONS_RECORD_AUDIO = 1000
    }
}