package com.example.appcitas.fragmen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.appcitas.R
import com.example.appcitas.databinding.FragmentInicioBinding


class InicioFragment : Fragment() {

private lateinit var binding: FragmentInicioBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.beteNDoctor.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_doctorFragment)
        }
        binding.beteNPaciente.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_pacienteFragment)
        }
    }

}