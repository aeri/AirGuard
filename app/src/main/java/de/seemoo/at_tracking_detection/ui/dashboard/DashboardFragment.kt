package de.seemoo.at_tracking_detection.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.seemoo.at_tracking_detection.R
import de.seemoo.at_tracking_detection.databinding.FragmentDashboardBinding
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel by viewModels()

    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dashboard,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = dashboardViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lineGraphChart = view.findViewById<RallyLineGraphChart>(R.id.line_graph)

        dashboardViewModel.getBeaconHistory(dateTime.minusDays(HISTORY_LENGTH))
            .observe(viewLifecycleOwner) { beaconList ->
                Timber.d("beacon list size: ${beaconList.size}")
                val dataPoints = mutableListOf<DataPoint>()
                (0..HISTORY_LENGTH).forEach { counter ->
                    val dayOfMonth = dateTime.minusDays(counter).dayOfMonth
                    val amount =
                        beaconList.filter { it.receivedAt.dayOfMonth == dayOfMonth }
                            .distinctBy { it.deviceAddress }.size
                    dataPoints.add(counter.toInt(), DataPoint(amount.toFloat()))
                }
                lineGraphChart.addDataPoints(dataPoints.reversed())
                Timber.d("Added ${dataPoints.size} new data points to the graph chart!")
            }

    }


    companion object {
        private val dateTime = LocalDateTime.now(ZoneOffset.UTC)
        private const val HISTORY_LENGTH = 14L
    }

}
