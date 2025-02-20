package com.example.appcitas.fragmen

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.appcitas.R
import com.example.appcitas.databinding.FragmentCitaBinding
import com.example.appcitas.databinding.FragmentEditarCItaBinding
import com.example.appcitas.dialogo.DateDialogo
import com.example.appcitas.dialogo.TimeDialogo
import com.example.appcitas.model.Cita
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID


class EditarCItaFragment : Fragment() {
    private lateinit var binding: FragmentEditarCItaBinding
    private val args: EditarCItaFragmentArgs by navArgs()
    private var selectedOption: String = ""

    private var selectedDate: String = ""

    private  var selectedTime: String = ""
    var idnumero: Int = 0
    private var selectedColor: Int = Color.BLACK
    private  var cita: Cita? = null

    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditarCItaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      cita = args.cita
        establecerdatos()
        seleccionarOpcion()
        binding.titulo.setText("Editar cita")
        binding.btnRegistrar.setText("Editar cita")
        reference = FirebaseDatabase.getInstance().getReference("Citas")
        // Configurar el botón para mostrar el diálogo de selección de fecha
        binding.BTNHORA.setOnClickListener {
            val dialogo = TimeDialogo()
            dialogo.show(parentFragmentManager, "timePicker")
            dialogo.listener = { hour, minute ->
                selectedTime = "$hour:$minute"
                binding.TITULOHORA.setText(selectedTime)
            }


        }
        // Configurar el botón para mostrar el diálogo de selección de fecha
        binding.BTNFECHA.setOnClickListener {
            val dialogo = DateDialogo()
            dialogo.show(parentFragmentManager, "datePicker")
            dialogo.listener = { year, month, dayOfMonth ->
                selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.TITULOOFECHA.setText(selectedDate)
            }
        }
        // Configurar el botón para mostrar el diálogo de selección de sexo
        binding.btnRegistrar.setOnClickListener {
            editarCita()
        }
        binding.btnSelectColor.setOnClickListener {
            showColorPickerDialog()
        }


    }

    private fun establecerdatos() {

        binding.nombreUsuario.setText(cita?.nombre)
        binding.apellidoUsuario.setText(cita?.apellido)
        binding.email.setText(cita?.email)
        binding.telefono.setText(cita?.fecha)
        //binding.mySpinner.setSelection(cita?.sexo)
        binding.TITULOOFECHA.setText(cita?.fecha)
        binding.TITULOHORA.setText(cita?.hora)


    }

    private fun seleccionarOpcion() {
        val options = listOf("masculino", "femenino")

        // Verificar que el binding está inicializado correctamente
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.mySpinner.adapter = adapter

        binding.mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Obtener la opción seleccionada
                selectedOption = parent?.getItemAtPosition(position).toString()

                // Mostrar un mensaje con la opción seleccionada
                Toast.makeText(
                    requireContext(),
                    "Seleccionaste: $selectedOption",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar el caso donde no se selecciona nada (opcional)
            }
        }
    }
    private fun showColorPickerDialog() {
        val colors = arrayOf("Rojo", "Verde", "Azul", "Amarillo", "Negro")
        val colorValues = arrayOf(
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BLACK,
            Color.WHITE, Color.CYAN, Color.MAGENTA, Color.GRAY, Color.DKGRAY,
            Color.LTGRAY, Color.TRANSPARENT
        )

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona un color")
            .setItems(colors) { _, which ->
                selectedColor = colorValues[which]
                Toast.makeText(requireContext(), selectedColor.toString(), Toast.LENGTH_SHORT).show()
                binding.selectedColorView.setTextColor(selectedColor)
                binding.selectedColorView.text = "Color seleccionado: ${colors[which]}"
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun editarCita() {

        val nombre = binding.nombreUsuario.text.toString()
        val apellido = binding.apellidoUsuario.text.toString()
        val email = binding.email.text.toString()
        val telefono = binding.telefono.text.toString()
        selectedDate = cita?.fecha.toString()
        selectedTime = cita?.hora.toString()

        val fecha = selectedDate
        val hora = selectedTime
        val sexo: String = selectedOption


        if (nombre.isNotEmpty() && apellido.isNotEmpty() && email.isNotEmpty()
            && telefono.isNotEmpty() && fecha.isNotEmpty() && hora.isNotEmpty()
            && sexo.isNotEmpty()
        ) {
            val random = UUID.randomUUID().toString()
            val cita = Cita(random, nombre, email, apellido, fecha, hora,
                sexo,String.format("#%08X", (selectedColor)), telefono )
            reference.child(cita.id).setValue(cita).addOnSuccessListener {
                binding.nombreUsuario.text?.clear()
                binding.apellidoUsuario.text?.clear()
                binding.email.text?.clear()
                binding.telefono.text?.clear()
                Toast.makeText(requireContext(), "Cita modificada exitosamente", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_editarCItaFragment_to_pacienteFragment)



            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error al registrar la cita", Toast.LENGTH_SHORT)
                    .show()
            }


        } else {
            Toast.makeText(
                requireContext(),
                "Por favor, completa todos los campos",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

}