package com.example.unisic_app.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Noticia
import com.example.unisic_app.data.model.VagaEmprego
import com.example.unisic_app.data.repository.FirebaseRepository
import com.google.firebase.firestore.ListenerRegistration
import com.example.unisic_app.ui.adapter.NoticiaAdapter
import com.example.unisic_app.ui.adapter.VagaAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val repository = FirebaseRepository()

    // Views
    private lateinit var tvHeader: TextView
    private lateinit var rvNoticias: RecyclerView
    private lateinit var rvVagas: RecyclerView

    // Adapters
    private lateinit var noticiaAdapter: NoticiaAdapter
    private lateinit var vagaAdapter: VagaAdapter

    private var noticiasListener: ListenerRegistration? = null
    private var vagasListener: ListenerRegistration? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Inicialização das Views
        tvHeader = view.findViewById(R.id.text_home_header)
        rvNoticias = view.findViewById(R.id.recycler_view_noticias)
        rvVagas = view.findViewById(R.id.recycler_view_vagas)

        //Configurar Adapters
        setupAdapters()

        //Carregar Dados do Firestore
        loadDataFromFirestore()
    }


    private fun setupAdapters() {
        val noticiaClickListener: (Noticia) -> Unit = { noticia ->
            abrirLink(noticia.url)
        }
        val vagaClickListener: (VagaEmprego) -> Unit = { vaga ->
            abrirLink(vaga.urlInscricao)
        }

        // Inicializa o adapter de Notícias com uma lista VAZIA.
        noticiaAdapter = NoticiaAdapter(emptyList(), noticiaClickListener)
        rvNoticias.layoutManager = LinearLayoutManager(context)
        rvNoticias.adapter = noticiaAdapter

        // Inicializa o adapter de Vagas com uma lista VAZIA.
        vagaAdapter = VagaAdapter(emptyList(), vagaClickListener)
        // Layout horizontal para vagas
        rvVagas.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvVagas.adapter = vagaAdapter
    }


    private fun loadDataFromFirestore() {
        // Inicia o listener de Notícias
        noticiasListener = repository.getNoticiasRealtime { noticias ->
            noticiaAdapter.updateList(noticias)
        }

        // Inicia o listener de Vagas
        vagasListener = repository.getVagasEmpregoRealtime { vagas ->
            vagaAdapter.updateList(vagas)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Para de ouvir o Firestore quando a View do Fragmento é destruída para evitar vazamento de memória
        noticiasListener?.remove()
        vagasListener?.remove()
        noticiasListener = null
        vagasListener = null
    }

    /**
     * Função utilitária para abrir um link externo no navegador.
     */
    private fun abrirLink(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Não foi possível abrir o link: $url", Toast.LENGTH_SHORT).show()
        }
    }
}