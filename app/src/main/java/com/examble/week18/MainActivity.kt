package com.examble.week18

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import bsh.Interpreter
import com.examble.week18.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var compositeDisposable: CompositeDisposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //call this function to get result
        calculator()


    }

    /* this function have observable and this observable use create operator
    * where create values myself
    * also it have specified thread by using rxandroid library to call Schedulers.io()
    * finally show a result in textview
    */
    private fun calculator() {
        compositeDisposable = CompositeDisposable()
        val observable = Observable.create<String> { emittor ->
            binding.input.doOnTextChanged { text, start, before, count ->
                emittor.onNext(text.toString())
            }
        }
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .debounce((1.5).toLong(), TimeUnit.SECONDS)
            .subscribe({ t ->
                Log.i(TAG, "on next: ${Thread.currentThread().name}")
                //show result in textView
                binding.result.text = operator(t)
            },
                { e ->
                    binding.result.text = e.message
                }).add(compositeDisposable)
    }

    //to make interpreter on the equation
    private fun operator(value: String): CharSequence {
        val interpreter = Interpreter()
        interpreter.eval("result = $value")
        return interpreter.get("result").toString()

    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    companion object {
        const val TAG = "AM_NAH"
    }

}


