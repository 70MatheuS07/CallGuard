import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Class.Friend
import com.ufes.callguard.R

/**
 * Adapter para exibição de uma lista de amigos em um RecyclerView.
 * @param context Contexto da aplicação.
 * @param friendsList Lista de amigos a ser exibida.
 * @param currentUserId ID do usuário atual.
 * @param onItemClick Lambda para ser chamado quando um item for clicado.
 */
class FriendsAdapter(
    private val context: Context,
    private val friendsList: List<Friend>,
    private val currentUserId: String,
    private val onItemClick: (Friend) -> Unit
) : RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_list, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendsList[position]
        holder.friendNameTextView.text = friend.userName
        holder.friendSwitch.isChecked = friend.isSelected

        holder.friendSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateFriendSelection(friend, isChecked)
        }

        holder.itemView.setOnClickListener {
            onItemClick(friend)
        }
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    private fun updateFriendSelection(friend: Friend, isSelected: Boolean) {
        val database = FirebaseFirestore.getInstance()
        val userDocRef = database.collection("usuario").document(currentUserId)

        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val friendListData = document.get("amigoList") as List<Map<String, Any>>?
                if (friendListData != null) {
                    val updatedFriendList = friendListData.toMutableList()
                    for (friendData in updatedFriendList) {
                        if (friendData["userName"] == friend.userName) {
                            (friendData as MutableMap<String, Any>)["selected"] = isSelected
                            break
                        }
                    }
                    userDocRef.update("amigoList", updatedFriendList)
                        .addOnSuccessListener {
                            // Optionally, handle success
                        }
                        .addOnFailureListener { e ->
                            // Optionally, handle failure
                        }
                }
            }
        }.addOnFailureListener { e ->
            // Optionally, handle failure
        }
    }

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendNameTextView: TextView = itemView.findViewById(R.id.friendNameTextView)
        val friendSwitch: Switch = itemView.findViewById(R.id.friendSwitch)
    }
}
