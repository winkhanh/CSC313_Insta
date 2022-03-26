package winkhanh.com.insta.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.parse.ParseFile
import winkhanh.com.insta.MainActivity
import winkhanh.com.insta.R
import winkhanh.com.insta.models.Post

class PostsAdapter(): RecyclerView.Adapter<PostsAdapter.ViewHolder>() {
    lateinit var context: Context
    private lateinit var posts: List<Post>
    val PROFILE_PIC_KEY = "profilePicture"
    constructor(context: Context,posts: List<Post>) : this() {
        this.context = context
        this.posts = posts
    }
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val ivImage : ImageView = itemView.findViewById(R.id.ivImage)
        val ivAvatar : ImageView = itemView.findViewById(R.id.ivAvatar)
        val tvAuthor : TextView = itemView.findViewById(R.id.tvAuthor)
        val tvTime : TextView = itemView.findViewById(R.id.tvTime)
        val tvDescription : TextView = itemView.findViewById(R.id.tvDescription)
        fun bind(post: Post){
            tvAuthor.text = post.getUser()?.username ?: "User"
            tvTime.text = post.getTime()
            val description = "<b>" + (post.getUser()?.username ?: "User") + "</b> " + (post.getDescription()?:"Description")
            val photoUrl : String = post.getUser()?.getParseFile(PROFILE_PIC_KEY)?.url ?: ""
            context?.let { Glide.with(it).load(photoUrl).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(ivAvatar) }
            tvDescription.text = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            ivAvatar.setOnClickListener {
                (context as MainActivity).goToProfile(post.getUser()?.objectId?:"")
            }
            val imageFile: ParseFile? = post.getImage()
            if (imageFile!=null)
                Glide.with(context).load(imageFile.url).into(ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}