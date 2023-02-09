package se.jbnu.yangdoplatform.Board

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import se.jbnu.yangdoplatform.R
import se.jbnu.yangdoplatform.model.FAQModel

data class FQABoardItem(val time:String, val title: String, val content: String)


class FQABoard : AppCompatActivity() {
    // 로그에 사용할 TAG 변수 선언
    private val TAG = javaClass.simpleName
    private var title_array_FAQ: ArrayList<FAQModel?> = ArrayList()
    private var searchView_fqa: SearchView? = null

    // 사용할 컴포넌트 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fqa_board)


        val fqa_category_namebtn1: Button = findViewById(R.id.fqa_category_namebtn1)
        val fqa_category_namebtn2: Button = findViewById(R.id.fqa_category_namebtn2)
        val fqa_category_namebtn3: Button = findViewById(R.id.fqa_category_namebtn3)
        val fqa_category_namebtn4: Button = findViewById(R.id.fqa_category_namebtn4)
        searchView_fqa = findViewById(R.id.search_view_fqa)

        val fqa_board_back_button : ImageButton = findViewById(R.id.fqa_board_back_button)
        fqa_board_back_button.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })


        // 고객센터에 문의하기 클릭 시 화면 전환
        val fqa_text_for_oneToneQnA : TextView = findViewById(R.id.fqa_text_for_oneToneQnA)

        fqa_text_for_oneToneQnA.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, OneToOneBoard::class.java)
            val activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.from_right, R.anim.to_left)
            startActivity(intent, activityOptions.toBundle())
        })





        val rv_FAQBoard = findViewById<RecyclerView>(R.id.fqa_category_recyclerview)
        val fAQAdapter = FQABoardAdapter()

        fAQAdapter.notifyDataSetChanged()

        rv_FAQBoard.adapter = fAQAdapter
        rv_FAQBoard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // 운영정책 버튼을 클릭할 시
        fqa_category_namebtn1.setOnClickListener(View.OnClickListener {
            fqaSelectedBtn("운영정책", fAQAdapter)
            rv_FAQBoard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        })
        fqa_category_namebtn2.setOnClickListener(View.OnClickListener {
            fqaSelectedBtn("계정/인증", fAQAdapter)
            rv_FAQBoard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        })
        fqa_category_namebtn3.setOnClickListener(View.OnClickListener {
            fqaSelectedBtn("구매/판매", fAQAdapter)
            rv_FAQBoard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        })
        fqa_category_namebtn4.setOnClickListener(View.OnClickListener {
            fqaSelectedBtn("거래 품목", fAQAdapter)
            rv_FAQBoard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        })



        // 검색어 입력 시
        searchView_fqa!!.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchedString: String): Boolean {
                // 목록 초기화를 위한 코드
                title_array_FAQ.clear()
                fAQAdapter.notifyDataSetChanged()


                // 파이어베이스 안에 있는 제목만 검색
                val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                val ref: DatabaseReference = database.getReference().child("FAQ")
                val query: Query = ref.orderByChild("title/").startAt(searchedString).endAt(searchedString + "\uf8ff")
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(@NonNull snapshot: DataSnapshot) {

                        for (dataSnapshot in snapshot.getChildren()) {
                            var faqTitleInFirebase: String = dataSnapshot.getValue(FAQModel::class.java)?.title.toString()
                            if(searchedString.equals(faqTitleInFirebase)){
                                title_array_FAQ.add(dataSnapshot.getValue(FAQModel::class.java))
                                Log.v("TESTININININ", faqTitleInFirebase)
                                println(title_array_FAQ)
                                fAQAdapter.notifyDataSetChanged()
                                rv_FAQBoard.layoutManager = LinearLayoutManager(this@FQABoard, LinearLayoutManager.VERTICAL, false)
                            }

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })
    }

    inner class FQABoardAdapter() :
        RecyclerView.Adapter<FQABoardAdapter.FQABoardViewHolder>() {


        var selectedCategoryName: String = ""
        val database = FirebaseDatabase.getInstance().reference
        val myRef = database.child("FAQ")


        init {

            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (item in dataSnapshot.children) {
                        title_array_FAQ.add(item.getValue(FAQModel::class.java))
                    }
                    // View를 새로고침해준다
                    notifyDataSetChanged()

                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
            // 새로고침 후 초기화를 해준다

        }

        constructor(selectedCategoryName: String): this(){
            this.selectedCategoryName = selectedCategoryName
            title_array_FAQ.clear()
            notifyDataSetChanged()

            println(title_array_FAQ)
            FirebaseDatabase.getInstance().reference.child("FAQ").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (item in dataSnapshot.children) {
                        var categoryNameInFirebase: String = item.getValue(FAQModel::class.java)?.category.toString()
                        if(selectedCategoryName.equals(categoryNameInFirebase)){
                            title_array_FAQ.add(item.getValue(FAQModel::class.java))
                            notifyDataSetChanged()
                            Log.v("TESTININININ", categoryNameInFirebase)
                            println(title_array_FAQ)
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
            println(title_array_FAQ)
        }



        private var fqaBoardTitle = ""


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FQABoardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fqa_board, parent, false)
            return FQABoardViewHolder(view)
        }

        override fun onBindViewHolder(holder: FQABoardViewHolder, position: Int) {
            val friendViewHolder = holder as FQABoardViewHolder

            holder.fqa_title.text = title_array_FAQ[position]?.title
            Log.v("TAGININ",position.toString())

            //아이템을 클릭 시 -  채팅방으로 이동(MessageActivity.class)
            friendViewHolder.itemView.setOnClickListener { view ->
                fqaBoardTitle = title_array_FAQ[position]?.title.toString()
//                fqaBoardTitle = itemList[position].title
                Log.v("TAGININ",fqaBoardTitle)
                val intent = Intent(view.context, Board_From_FAQ_Activity::class.java)
                intent.putExtra("clickedFQATitle", fqaBoardTitle)
                val activityOptions = ActivityOptions.makeCustomAnimation(view.context, R.anim.from_right, R.anim.to_left)
                startActivity(intent, activityOptions.toBundle())
            }

        }

        override fun getItemCount(): Int {
            return title_array_FAQ.size
        }


        inner class FQABoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val fqa_title = itemView.findViewById<TextView>(R.id.fqa_board_title_item)
        }
    }


    fun fqaSelectedBtn(selectedCategoryName: String, fAQAdapter: FQABoardAdapter){
        title_array_FAQ.clear()
        fAQAdapter.notifyDataSetChanged()

        println(title_array_FAQ)
        FirebaseDatabase.getInstance().reference.child("FAQ").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (item in dataSnapshot.children) {
                    var categoryNameInFirebase: String = item.getValue(FAQModel::class.java)?.category.toString()
                    if(selectedCategoryName.equals(categoryNameInFirebase)){
                        title_array_FAQ.add(item.getValue(FAQModel::class.java))
                        fAQAdapter.notifyDataSetChanged()
                        Log.v("TESTININININ", categoryNameInFirebase)
                        println(title_array_FAQ)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


}



