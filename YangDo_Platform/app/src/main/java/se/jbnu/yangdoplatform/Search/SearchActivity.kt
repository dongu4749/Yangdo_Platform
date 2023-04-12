package se.jbnu.yangdoplatform.Search

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import se.jbnu.yangdoplatform.R
import java.util.*

class SearchActivity : AppCompatActivity() {
    private var searchView: SearchView? = null
    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchView = findViewById(R.id.search_view_board)
        listView = findViewById(R.id.searchResult_list_board)

        // 검색 결과를 넣기 위한 변수
        val list: MutableList<String> = ArrayList()
        val adapter: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(this, android.R.layout.simple_list_item_1, list as List<Any?>)
        searchView!!.setOnQueryTextListener(object : OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                // 목록 초기화를 위한 코드
                list.clear()
                adapter.notifyDataSetChanged()

                // 파이어베이스 안에 있는 제목만 검색
                val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                val ref: DatabaseReference = database.getReference().child("board")
                val query: Query = ref.orderByChild("title/").startAt(s).endAt(s + "\uf8ff")
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                        adapter.notifyDataSetChanged()
                        for (dataSnapshot in snapshot.getChildren()) {
                            val title: String =
                                dataSnapshot.child("title").getValue(String::class.java)!!
                            val content: String = dataSnapshot.child("content").getValue(
                                String::class.java
                            )!!
                            // 리스트뷰에 검색 목록 나열하기 위한 코드
                            list.add(title)
                            listView!!.adapter = adapter
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
}