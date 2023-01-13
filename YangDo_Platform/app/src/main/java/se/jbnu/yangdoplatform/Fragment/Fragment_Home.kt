package se.jbnu.yangdoplatform.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import se.jbnu.yangdoplatform.Board.Board_RegisterActivity
import se.jbnu.yangdoplatform.R

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment_Home : Fragment(), View.OnClickListener {
    private var fab_open: Animation? = null
    private var fab_close: Animation? = null
    private var isFabOpen = false
    private var fab_main: FloatingActionButton? = null
    private var fab_home: FloatingActionButton? = null
    private var fab_gym: FloatingActionButton? = null
    private var fab_register: FloatingActionButton? = null

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {

                // Handle the back button event
                val builder = AlertDialog.Builder(activity!!)
                builder.setTitle("앱 종료")
                builder.setMessage("정말 앱을 종료하시겠습니까?")
                builder.setPositiveButton("OK") { dialog, which -> activity!!.finish() }
                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.create().show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment__home, container, false)
        fab_open = AnimationUtils.loadAnimation(activity!!.applicationContext, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(activity!!.applicationContext, R.anim.fab_close)
        fab_main = v.findViewById<View>(R.id.fab_main) as FloatingActionButton
        fab_home = v.findViewById<View>(R.id.fab_home) as FloatingActionButton
        fab_gym = v.findViewById<View>(R.id.fab_gym) as FloatingActionButton
        fab_register = v.findViewById<View>(R.id.fab_register) as FloatingActionButton
        fab_main!!.setOnClickListener(this)
        fab_home!!.setOnClickListener(this)
        fab_gym!!.setOnClickListener(this)
        fab_register!!.setOnClickListener(this)
        return v
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.fab_main -> anim()
            R.id.fab_home -> anim()
            R.id.fab_gym -> anim()
            R.id.fab_register -> {
                anim()
                val intent = Intent(activity!!.applicationContext, Board_RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun anim() {
        if (isFabOpen) {
            fab_main!!.setImageResource(R.drawable.ic_baseline_add_24)
            fab_home!!.startAnimation(fab_close)
            fab_gym!!.startAnimation(fab_close)
            fab_register!!.startAnimation(fab_close)
            fab_home!!.isClickable = false
            fab_gym!!.isClickable = false
            fab_register!!.isClickable = false
            isFabOpen = false
        } else {
            fab_main!!.setImageResource(R.drawable.ic_baseline_close_24)
            fab_home!!.startAnimation(fab_open)
            fab_gym!!.startAnimation(fab_open)
            fab_register!!.startAnimation(fab_open)
            fab_home!!.isClickable = true
            fab_gym!!.isClickable = true
            fab_register!!.isClickable = true
            isFabOpen = true
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(): Fragment_Home {
            return Fragment_Home()
        }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment_Home.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Fragment_Home {
            val fragment = Fragment_Home()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}