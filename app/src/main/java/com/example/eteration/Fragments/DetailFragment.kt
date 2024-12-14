package com.example.eteration.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.eteration.Models.Product
import com.example.eteration.Utility.UrlFotoDownload
import com.example.eteration.ViewModels.MainActivityViewModel
import com.example.eteration.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var binding: FragmentDetailBinding
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            product = DetailFragmentArgs.fromBundle(it).product
        }

        UrlFotoDownload.urlFotoSetImage(binding.productImageView, product.image)
        binding.itemTitle.text = product.name
        binding.productBrandText.text = product.brand
        binding.productModelText.text = product.model
        binding.productNameText.text = product.name
        binding.productExplainText.text = product.description
        binding.productPriceText.text = product.price

        binding.addPackageButton.setOnClickListener {
            mainActivityViewModel.updatePackagesList(product, true)
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}