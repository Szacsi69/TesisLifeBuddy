package hu.bme.aut.android.tesislifebuddy.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.tesislifebuddy.R
import hu.bme.aut.android.tesislifebuddy.databinding.FragmentItemSearchBinding

class ItemSearchFragment : DialogFragment(){
    private lateinit var listener: ItemSearchedListener
    private lateinit var binding: FragmentItemSearchBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = if (targetFragment != null) {
                targetFragment as ItemSearchedListener
            } else {
                activity as ItemSearchedListener
            }
        } catch (e: ClassCastException) {
            throw RuntimeException(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.GreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentItemSearchBinding.inflate(inflater, container, false)
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

            else {
                listener.onItemSearched(
                    binding.etNameInput.text.toString()
                )
                dismiss()
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    interface ItemSearchedListener {
        fun onItemSearched(name: String)
    }
}
