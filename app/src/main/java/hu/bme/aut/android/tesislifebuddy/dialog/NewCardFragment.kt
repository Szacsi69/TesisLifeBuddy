package hu.bme.aut.android.tesislifebuddy.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.tesislifebuddy.R
import hu.bme.aut.android.tesislifebuddy.databinding.FragmentNewCardBinding

class NewCardFragment : DialogFragment() {
    private lateinit var listener: NewCardCreatedListener
    private lateinit var binding: FragmentNewCardBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = if (targetFragment != null) {
                targetFragment as NewCardCreatedListener
            } else {
                activity as NewCardCreatedListener
            }
        } catch (e: ClassCastException) {
            throw RuntimeException(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.GreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewCardBinding.inflate(inflater, container, false)
        dialog?.setTitle(R.string.new_card)
        dialog?.window?.setWindowAnimations(R.style.dialog_animation_fade);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnOk.setOnClickListener {
            if (binding.etNameInput.text.toString() == "") {
                binding.etNameInput.error = getString(R.string.error)
            }
            else if (binding.etQuantityInput.text.toString() == "") {
                binding.etQuantityInput.error = getString(R.string.error)
            }
            else if (binding.etCalorieInput.text.toString() == "") {
                binding.calorieInput.error = getString(R.string.error)
            }
            else if (binding.etProteinInput.text.toString() == "") {
                binding.proteinInput.error = getString(R.string.error)
            }
            else {
                listener.onNewCardCreated(
                    binding.etNameInput.text.toString(),
                    binding.etQuantityInput.text.toString(),
                    binding.etCalorieInput.text.toString().toFloat(),
                    binding.etProteinInput.text.toString().toFloat(),
                    if (binding.etBrandInput.text.toString() == "") "-"
                    else binding.etBrandInput.text.toString()
                )
                dismiss()
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    interface NewCardCreatedListener {
        fun onNewCardCreated(name: String, quantity: String, calorie: Float, protein: Float, brand: String)
    }
}