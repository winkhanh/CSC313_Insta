package winkhanh.com.insta.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser
import winkhanh.com.insta.EndlessRecyclerViewScrollListener
import winkhanh.com.insta.R
import winkhanh.com.insta.adapters.PostsAdapter
import winkhanh.com.insta.models.Post

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] and [HomeFragment.newInstanceWithId] factory method to
 * create an instance of this fragment.
 */
class FeedFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var userId: String? = null
    var page: Int = 0
    lateinit var rvPosts : RecyclerView
    var posts : MutableList<Post> = mutableListOf()
    lateinit var swipeContainer: SwipeRefreshLayout
    lateinit var adapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            FeedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, null)
                }
            }
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param id Parameter 1.
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstanceWithId(id: String) =
            FeedFragment().apply {
                Log.d("CREATE",id)
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, id)
                }
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvPosts = view.findViewById(R.id.rvPosts)
        Log.d("Feed",userId?:"no id")
        swipeContainer = view.findViewById(R.id.swipeContainer)
        swipeContainer.scrollTo(0,0)
        adapter = context?.let { PostsAdapter(it,posts) }!!
        rvPosts.adapter = adapter
        if (userId!=null && userId!=ParseUser.getCurrentUser().objectId){
            val layoutManager = GridLayoutManager(context,3)
            rvPosts.layoutManager = GridLayoutManager(context,3)
            rvPosts.addOnScrollListener(object:EndlessRecyclerViewScrollListener(layoutManager){
                override fun onLoadMore(pages: Int, totalItemsCount: Int, view: RecyclerView?) {
                    page += 1
                    queryPosts()
                }

            })
        }else{
            val layoutManager = LinearLayoutManager(context)
            rvPosts.layoutManager = LinearLayoutManager(context)
            rvPosts.addOnScrollListener(object:EndlessRecyclerViewScrollListener(layoutManager){
                override fun onLoadMore(pages: Int, totalItemsCount: Int, view: RecyclerView?) {
                    page += 1
                    queryPosts()
                }

            })
        }

        swipeContainer.setOnRefreshListener {
            page=0
            queryPosts()
        }
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        queryPosts()

    }

    private fun queryPosts(){
        val query : ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        query.limit=20
        query.skip=page*20
        query.addDescendingOrder(Post.CREATE_AT)

        if (userId != null){
            val userQuery : ParseQuery<ParseUser> = ParseQuery.getQuery(ParseUser::class.java)
            userQuery.whereEqualTo(ParseUser.KEY_OBJECT_ID,userId)
            query.whereMatchesQuery(Post.KEY_USER, userQuery)
        }
        query.findInBackground(object: FindCallback<Post>{
            override fun done(fetchedPosts: MutableList<Post>?, e: ParseException?) {
                if (e!=null){
                    Log.e("Home","Issue with getting posts",e)
                    return
                }
                swipeContainer.isRefreshing=false
                val precount = adapter.itemCount
                if (page==0)
                    posts.clear()
                adapter.notifyItemRangeRemoved(0,precount)
                if (fetchedPosts != null) {
                    posts.addAll(fetchedPosts)
                }
                adapter.notifyDataSetChanged()
            }
        })
    }
}