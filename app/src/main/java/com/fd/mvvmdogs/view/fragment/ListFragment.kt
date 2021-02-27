package com.fd.mvvmdogs.view.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.fd.mvvmdogs.R
import com.fd.mvvmdogs.adapter.DogsListAdapter
import com.fd.mvvmdogs.databinding.FragmentListBinding
import com.fd.mvvmdogs.viewmodel.ListViewModel


class ListFragment : Fragment(R.layout.fragment_list) {

    private var _binding: FragmentListBinding? = null

    private lateinit var viewModel : ListViewModel
    private val dogsListAdapter = DogsListAdapter(arrayListOf())

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        //todo
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        viewModel.refresh()

        binding.rvDogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogsListAdapter
        }

        binding.apply {
            refreshLayout.setOnRefreshListener {
                rvDogsList.visibility=View.GONE
                tvListError.visibility =View.GONE
                pbLoadingView.visibility =View.VISIBLE
                viewModel.refreshBypassCache()
                refreshLayout.isRefreshing = false

            }
        }

        observeViewModel()
    }

    fun observeViewModel(){
        viewModel.dogs.observe(viewLifecycleOwner, Observer {dogs ->  //List<DogBreed>!
            dogs?.let {
                binding.rvDogsList.visibility =View.VISIBLE
                dogsListAdapter.updateDogList(dogs)

                binding.tvListError.visibility =View.GONE
                binding.pbLoadingView.visibility =View.GONE

            }
        })
        viewModel.dogsLoadError.observe(viewLifecycleOwner , Observer { isError -> //it:Boolean
            isError?.let {
                if (isError){
                    binding.tvListError.visibility =View.VISIBLE
                }
                else{
                    binding.tvListError.visibility =View.GONE
                }

            }
        })
        viewModel.loading.observe(viewLifecycleOwner , Observer { isLoading -> //it Boolean
            isLoading?.let {
                binding.pbLoadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
                if (it){
                    binding.tvListError.visibility =View.GONE
                    binding.rvDogsList.visibility =View.GONE
                }
            }

        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.actionSettings ->{
                view?.let { Navigation.findNavController(it).navigate(ListFragmentDirections.actionListFragmentToSettingsFragment())
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}