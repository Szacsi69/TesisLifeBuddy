package hu.bme.aut.android.tesislifebuddy.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.tesislifebuddy.FoodAddedListener
import hu.bme.aut.android.tesislifebuddy.R
import hu.bme.aut.android.tesislifebuddy.databinding.FragmentAddToCountersBinding

class AddToCountersFragment : DialogFragment() {
    private lateinit var listener: FoodAddedListener
    private lateinit var binding: FragmentAddToCountersBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = activity as FoodAddedListener

        } catch (e: ClassCastException) {
            throw RuntimeException(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.GreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddToCountersBinding.inflate(inflater, container, false)
        dialog?.setTitle(R.string.add_to_counters)
        dialog?.window?.setWindowAnimations(R.style.dialog_animation_fade);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnOk.setOnClickListener {
            if (binding.etCalorieInput.text.toString() == "") {
                binding.calorieInput.error = getString(R.string.error)
            }
            else if (binding.etProteinInput.text.toString() == "") {
                binding.proteinInput.error = getString(R.string.error)
            }
            else {
                listener.onFoodAdded(
                    binding.etCalorieInput.text.toString().toFloat(),
                    binding.etProteinInput.text.toString().toFloat()
                )
                dismiss()
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

}