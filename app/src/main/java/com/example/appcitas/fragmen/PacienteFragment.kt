package com.example.appcitas.fragmen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcitas.R
import com.example.appcitas.addaptercita.AdapterCita
import com.example.appcitas.databinding.FragmentInicioBinding
import com.example.appcitas.databinding.FragmentPacienteBinding
import com.example.appcitas.model.Cita
import com.example.appcitas.model.Delete
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PacienteFragment : Fragment() {

private lateinit var binding: FragmentPacienteBinding
private lateinit var madapter: AdapterCita
    private lateinit var reference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPacienteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reference = FirebaseDatabase.getInstance().getReference("Citas")
        binding.AGREGARCITAS.setOnClickListener {
            findNavController().navigate(R.id.action_pacienteFragment_to_citaFragment)

        }
        resycler()
        obtenerCitas()
        eliminar()
        editarCita()


    }
    private fun editarCita() {
        madapter.citasselecionadas = {
            val bundle = Bundle().apply {
                putParcelable("cita", it)
            }
            findNavController().navigate(R.id.action_pacienteFragment_to_editarCItaFragment, bundle)
        }
    }
    private fun resycler() {
        madapter = AdapterCita()
        binding.RECYCLERPACIENTE.apply {
            adapter = madapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }
    private fun obtenerCitas() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val citalis = mutableListOf<Cita>()
                if (snapshot.exists()) {
                    for (cita in snapshot.children) {
                        val cita = cita.getValue(Cita::class.java)
                        cita?.let {
                            citalis.add(it)
                        }

                    }
                    //madapter.setData(citalis)
                    madapter.differ.submitList(citalis)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun eliminar(){
        val delete = object : Delete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val cita = madapter.differ.currentList[position]
                reference.child(cita.id).removeValue()
            }
        }
        ItemTouchHelper(delete).attachToRecyclerView(binding.RECYCLERPACIENTE)
    }
}