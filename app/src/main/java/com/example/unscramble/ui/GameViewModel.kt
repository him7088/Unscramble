package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class GameViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState : StateFlow<GameUiState> = _uiState.asStateFlow()
    private lateinit var currentWord : String
    private val usedWords : MutableSet<String> = mutableSetOf()

    var userGuess by mutableStateOf("")
        private set
    init {
        resetGame()
    }

    //Start or Reset the game
    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambleWord = pickRandomWordAndShuffle())
    }


    // Pick a random word from the dataset of words
    private fun pickRandomWordAndShuffle() : String {
        currentWord = allWords.random()
        if(currentWord in usedWords) {
            return pickRandomWordAndShuffle()
        }
        else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    // Shuffle the word picked from pickRandomWordAndShuffle()
    private fun shuffleCurrentWord(word : String) : String {
        val tempWord = word.toCharArray()
        //Scramble the word
        tempWord.shuffle()

        while( String(tempWord) == word ) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    //Update user guess
    fun updateUserGuess(guessWord : String) {
        userGuess = guessWord
    }

    fun checkUserGuess() {
        if(userGuess.equals(currentWord , ignoreCase = true)) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        }
        else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        updateUserGuess("")
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)
    }
    private fun updateGameState(updatedScore : Int) {
        if(usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        }
        else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambleWord = pickRandomWordAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
        }
    }


}