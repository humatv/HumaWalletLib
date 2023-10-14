package ir.huma.humawallet.lib

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast


class HumaWallet {
    private val receive = "ir.huma.humawallet.paystatus"

    private var activity: Activity? = null
    private var paymentToken: String? = null
        get

    private var isFastPayment = false

    private var onPayListener: OnPayListener? = null

    constructor(activity: Activity?) {
        this.activity = activity
    }

    fun setPaymentToken(token: String?): HumaWallet {
        this.paymentToken = token
        return this
    }

    fun setFastPayment(isFast: Boolean): HumaWallet {
        this.isFastPayment = isFast
        return this
    }

    fun getContext(): Activity? {
        return activity
    }

    fun getOnPayListener(): OnPayListener? {
        return onPayListener
    }

    fun setOnPayListener(onPayListener: OnPayListener?): HumaWallet {
        this.onPayListener = onPayListener
        return this
    }


    fun send() {
        if (paymentToken.isNullOrEmpty()) {
            throw RuntimeException("please set paymentToken!!!")
        }
        if (onPayListener == null) {
            throw RuntimeException("please setOnPayListener in java code!!!")
        }
        if (!checkHumaInstalled()) {
            Toast.makeText(
                activity,
                "لطفا ابتدا برنامه هوما استور را نصب کنید.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        sendPay()
    }

    private fun sendPay() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("app://wallet.huma.ir"))
        intent.putExtra("token", paymentToken)
        intent.putExtra("isFastPayment", isFastPayment)
        intent.putExtra("package", activity?.packageName)
        intent.setPackage("ir.huma.humastore")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("taskId", activity?.taskId)
        activity!!.startActivity(intent)
        activity!!.registerReceiver(receiver, IntentFilter(receive))
    }


    fun unregiter() {
        try {
            getContext()!!.unregisterReceiver(receiver)
        } catch (e: Exception) {
        }
    }


    private fun checkHumaInstalled(): Boolean {
        return try {
            getContext()!!.packageManager.getPackageInfo("ir.huma.humastore", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            //e.printStackTrace();
            false
        }
    }


    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            try {
//                var fIntent = Intent(activity, activity?.javaClass)
//                fIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
//                fIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                fIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
//                activity?.startActivity(fIntent)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

            try {
                if (onPayListener != null) {
                    if ((intent.hasExtra("packageName") && intent.getStringExtra("packageName") == getContext()!!.packageName)
                        || !intent.hasExtra("packageName")
                    ) {
                        if (intent.getBooleanExtra("success", false)) {
                            onPayListener!!.onPayComplete(intent.getStringExtra("message"))
                        } else {
                            onPayListener!!.onPayFail(intent.getStringExtra("message"))
                        }
                        try {
                            getContext()!!.unregisterReceiver(this)
                        } catch (e: Exception) {
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    interface OnPayListener {
        fun onPayComplete(code: String?)
        fun onPayFail(message: String?)
    }
}