import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Class.Amigo
import com.ufes.callguard.R

class FriendsAdapter(
    private val context: Context,
    private val friendsList: List<Amigo>,
    private val currentUserId: String
) : RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.friend_list, parent, false)
        return FriendsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val currentFriend = friendsList[position]
        holder.userNameTextView.text = currentFriend.userName
        holder.friendSwitch.isChecked = currentFriend.isSelected

        holder.friendSwitch.setOnCheckedChangeListener { _, isChecked ->
            currentFriend.isSelected = isChecked
            updateFriendSelection(currentFriend)
        }
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    private fun updateFriendSelection(amigo: Amigo) {
        val database = FirebaseFirestore.getInstance()
        val currentUserRef = database.collection("usuario").document(currentUserId)

        currentUserRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val amigoListData = documentSnapshot.get("amigoList") as? List<Map<String, Any>>?
                    val updatedAmigoList = amigoListData?.map {
                        val userName = it["userName"] as String? ?: ""
                        val isSelected = it["isSelected"] as Boolean? ?: false
                        if (userName == amigo.userName) {
                            mapOf("userName" to userName, "isSelected" to amigo.isSelected)
                        } else {
                            it
                        }
                    } ?: emptyList()

                    currentUserRef.update("amigoList", updatedAmigoList)
                        .addOnSuccessListener {
                            // Atualização bem-sucedida
                        }
                        .addOnFailureListener { exception ->
                            // Tratar falha na atualização
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Tratar falha na leitura do documento
            }
    }

    inner class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.friendNameTextView)
        val friendSwitch: Switch = itemView.findViewById(R.id.friendSwitch)
    }
}
