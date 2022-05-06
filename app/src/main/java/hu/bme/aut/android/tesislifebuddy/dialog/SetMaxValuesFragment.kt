package hu.bme.aut.android.tesislifebuddy.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.tesislifebuddy.R
import hu.bme.aut.android.tesislifebuddy.databinding.FragmentSetMaxValuesBinding


class SetMaxValuesFragment : DialogFragment() {
    private lateinit var listener: MaxValuesChangedListener
    private lateinit var binding: FragmentSetMaxValuesBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = if (targetFragment != null) {
                targetFragment as MaxValuesChangedListener
            } else {
                activity as MaxValuesChangedListener
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
        binding = FragmentSetMaxValuesBinding.inflate(inflater, container, false)
        dialog?.setTitle(R.string.fragment_set_max_values_title)
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
                listener.onMaxValuesChanged(
                    Integer.valueOf(binding.etCalorieInput.text.toString()),
                    Integer.valueOf(binding.etProteinInput.text.toString())
                )
                dismiss()
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    interface MaxValuesChangedListener {
        fun onMaxValuesChanged(cMax: Int, pMax: Int)
    }
}