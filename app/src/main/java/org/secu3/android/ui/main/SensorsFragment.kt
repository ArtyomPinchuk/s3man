/* SecuDroid  - An open source, free manager for SECU-3 engine control unit
   Copyright (C) 2020 Vitaliy O. Kosharskiy. Ukraine, Kharkiv

   SECU-3  - An open source, free engine control unit
   Copyright (C) 2007 Alexey A. Shabelnikov. Ukraine, Kyiv

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.

   contacts:
              http://secu-3.org
              email: vetalkosharskiy@gmail.com
*/

package org.secu3.android.ui.main

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import org.secu3.android.R
import org.secu3.android.databinding.FragmentSensorsBinding
import org.secu3.android.ui.settings.SettingsActivity
import org.secu3.android.utils.Task
import org.secu3.android.utils.gone
import org.secu3.android.utils.visible
import java.util.*

@AndroidEntryPoint
class SensorsFragment : Fragment() {

    private lateinit var mBinding: FragmentSensorsBinding

    private val mViewModel: SensorsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            exit()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentSensorsBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        mViewModel.connectionStatusLiveData.observe(viewLifecycleOwner) {
            if (it) {
                mBinding.connectionStatus.text = getString(R.string.status_online)
            } else {
                mBinding.connectionStatus.text = getString(R.string.status_offline)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        when {
            mViewModel.isBluetoothDeviceAddressNotSelected() -> {
                Toast.makeText(context, R.string.choose_bluetooth_adapter, Toast.LENGTH_LONG).show()
            }
            !BluetoothAdapter.getDefaultAdapter().isEnabled -> {
                Toast.makeText(context, R.string.msg_bluetooth_disabled, Toast.LENGTH_LONG).show()
            }
            else -> {
                mViewModel.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.closeConnection()
    }

    private fun init() {

        mBinding.toolbar.apply {
            inflateMenu(R.menu.activity_main)

            setOnMenuItemClickListener { onMenuItemSelected(it) }
            mViewModel.sendNewTask(Task.Secu3ReadSensors)
        }

        mBinding.fwInfo.setOnClickListener {
            Snackbar.make(mBinding.root, "${mBinding.fwInfo.text}", Snackbar.LENGTH_LONG).show()
        }

        /*mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { mViewModel.sendNewTask(Task.Secu3ReadSensors)
                        mBinding.sensors.visible()
                        mBinding.rawSensorInfo.gone()
                    }
                    else -> { mViewModel.sendNewTask(Task.Secu3ReadRawSensors)
                        mBinding.sensors.gone()
                        mBinding.rawSensorInfo.visible()
                    }

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })*/


        mViewModel.firmwareLiveData.observe(viewLifecycleOwner) {
            mBinding.fwInfo.text = it.tag
            mViewModel.sendNewTask(Task.Secu3ReadSensors)
        }

        mViewModel.sensorsLiveData.observe(viewLifecycleOwner) {
            mBinding.apply {
                freq.title.text = "Обороты, об/мин:"
                freq.value.text = it.frequen.toString()
                freq.value.setTextColor(if (it.frequen <=500) Color.RED else Color.GREEN)

                pressure.title.text = "Абсолютное давление, кПа:"
                pressure.value.text = String.format(Locale.US, "%.1f", it.pressure)

                voltage.title.text = "Напряжение борт. сети, В:"
                voltage.value.text = String.format(Locale.US, "%.1f", it.voltage)
                voltage.value.setTextColor(if (it.voltage <= 11.9f) Color.RED else Color.GREEN)

                temperature.title.text = "Температура ОЖ, °C:"
                temperature.value.text =  if (it.temperature < -40f) "-40" else String.format(Locale.US, "%.1f", it.temperature)

                advAngle.title.text = "Угол опережения, град.:"
                advAngle.value.text = String.format(Locale.US, "%.1f", it.advAngle)

                knockRetard.title.text = "Корр. УОЗ от ДД, град.:"
                knockRetard.value.text = String.format(Locale.US, "%.1f", it.knockRetard)

                knockValue.title.text = "Сигнал детонации, В:"
                knockValue.value.text = String.format(Locale.US, "%.3f", it.knockValue)

                airFlow.title.text = "Расход воздуха, N кривой:"
                airFlow.value.text = it.airflow.toString()

                tps.title.text = "Дроссельная заслонка, %:"
                tps.value.text = String.format(Locale.US, "%.1f", it.tps)

                add1.title.text = "Вход 1, В:"
                add1.value.text = String.format(Locale.US, "%.3f", it.addI1)

                add2.title.text = "Вход 2, В:"
                add2.value.text = String.format(Locale.US, "%.3f", it.addI2)

                chokePosition.title.text = "ВЗ/РДВ, %:"
                chokePosition.value.text = String.format(Locale.US, "%.1f", it.chokePosition)

                gasDosePosition.title.text = "Дозатор газа, %: "
                gasDosePosition.value.text = it.gasDosePosition.toString()

                synthLoad.title.text = "Синтет. нагрузка:"
                synthLoad.value.text = String.format(Locale.US, "%.1f", it.load)

                speed.title.text = "Скорость авто, км/ч:"
                speed.value.text = String.format(Locale.US, "%.1f", it.getSpeed())

                distance.title.text = "Пробег, км:"
                distance.value.text = String.format(Locale.US, "%.1f", it.getDistance())

                fuelInj.title.text = "Расход топлива, Гц:"
                fuelInj.value.text = it.fuelInject.toString()

                airTemp.title.text = "Температура ДТВ, °C:"
                airTemp.value.text = String.format(Locale.US, "%.1f", it.airTempSensor)

                lambdaCorr.title.text = "Лямбда-коррекция, %:"
                lambdaCorr.value.text = String.format(Locale.US, "%.2f", it.lambdaCorr)

                injPw.title.text = "Длительность впрыска, мс:"
                injPw.value.text = String.format(Locale.US, "%.2f", it.injPw)

                tpsDot.title.text = "Скорость ДЗ, %/сек:"
                tpsDot.value.text = it.tpsdot.toString()

                map2.title.text = "ДАД 2, кПа:"
                map2.value.text = String.format(Locale.US, "%.1f", it.map2)

                mapDiff.title.text = "Дифф. давление, кПа:"
                mapDiff.value.text = String.format(Locale.US, "%.1f", it.mapDiff)

                tmp2.title.text = "ДТВ 2, °C:"
                tmp2.value.text = String.format(Locale.US, "%.1f", it.tmp2)

                afr.title.text = "Воздух/топливо ШДК, в/т:"
                afr.value.text = String.format(Locale.US, "%.1f", it.afr)

                consFuel.title.text = "Расход топлива, Л/100км:"
                consFuel.value.text = String.format(Locale.US, "%.2f", it.consFuel)

                grts.title.text = "ДТГР, °C:"
                grts.value.text = String.format(Locale.US, "%.1f", it.grts)

                ftls.title.text = "Уровень топлива, Л:"
                ftls.value.text = String.format(Locale.US, "%.1f", it.ftls)

                egts.title.text = "Темп. выхл. газов, °C:"
                egts.value.text = String.format(Locale.US, "%.1f", it.egts)

                ops.title.text = "Давление масла, кг/см2:"
                ops.value.text = String.format(Locale.US, "%.1f", it.ops)

                injDuty.title.text = "Загрузка форсунок, %:"
                injDuty.value.text = String.format(Locale.US, "%.1f", it.injDuty)

                maf.title.text = "ДМРВ, г/сек:"
                maf.value.text = String.format(Locale.US, "%.1f", it.maf)

                ventDuty.title.text = "Скважность Вент., %:"
                ventDuty.value.text = it.ventDuty.toString()

                mapDot.title.text = "Скорость ДАД, %/сек:"
                mapDot.value.text = it.mapDot.toString()

                fts.title.text = "Температура топлива, °C:"
                fts.value.text = String.format(Locale.US, "%.1f", it.fts)

                lambdaCorr2.title.text = "Лямбда-коррекция 2, %:"
                lambdaCorr2.value.text = String.format(Locale.US, "%.1f", it.lambdaCorr2)

                afr2.title.text = "Воздух/топливо ШДК2, в/т:"
                afr2.value.text = String.format(Locale.US, "%.1f", it.afr2)

                statusGasDosThrottleFlFuel.status1.text = "Газовый клапан"
                statusGasDosThrottleFlFuel.status1.setBackgroundColor(if(it.gasBit > 0) Color.GREEN else Color.LTGRAY)
                statusGasDosThrottleFlFuel.status2.text = "Дроссель"
                statusGasDosThrottleFlFuel.status2.setBackgroundColor(if(it.carbBit > 0) Color.GREEN else Color.LTGRAY)
                statusGasDosThrottleFlFuel.status3.text = "Топл. при ПХХ"
                statusGasDosThrottleFlFuel.status3.setBackgroundColor(if(it.ephhValveBit > 0) Color.GREEN else Color.LTGRAY)

                statusPowerValveStarterAe.status1.text = "Клапан ЭМР"
                statusPowerValveStarterAe.status1.setBackgroundColor(if(it.epmValveBit > 0) Color.GREEN else Color.LTGRAY)
                statusPowerValveStarterAe.status2.text = "Блокир. стартера"
                statusPowerValveStarterAe.status2.setBackgroundColor(if(it.stBlockBit > 0) Color.GREEN else Color.LTGRAY)
                statusPowerValveStarterAe.status3.text = "Обогащ. при уск."
                statusPowerValveStarterAe.status3.setBackgroundColor(if(it.accelerationBit > 0) Color.GREEN else Color.LTGRAY)

                statusCoolingFanCheckEngineRevLimFuelCut.status1.text = "Вентилятор"
                statusCoolingFanCheckEngineRevLimFuelCut.status1.setBackgroundColor(if(it.coolFanBit > 0) Color.GREEN else Color.LTGRAY)
                statusCoolingFanCheckEngineRevLimFuelCut.status2.text = "Check Engine"
                statusCoolingFanCheckEngineRevLimFuelCut.status2.setBackgroundColor(if(it.checkEngineBit > 0) Color.GREEN else Color.LTGRAY)
                statusCoolingFanCheckEngineRevLimFuelCut.status3.text = "Отсеч. топл. по обр."
                statusCoolingFanCheckEngineRevLimFuelCut.status3.setBackgroundColor(if(it.fcRevlimBit > 0) Color.GREEN else Color.LTGRAY)

                statusFloodClearSysLockIgnInput.status1.text = "Продувка двиг."
                statusFloodClearSysLockIgnInput.status1.setBackgroundColor(if(it.floodClearBit > 0) Color.GREEN else Color.LTGRAY)
                statusFloodClearSysLockIgnInput.status2.text = "Система заблок."
                statusFloodClearSysLockIgnInput.status2.setBackgroundColor(if(it.sysLockedBit > 0) Color.GREEN else Color.LTGRAY)
                statusFloodClearSysLockIgnInput.status3.text = "Вход IGN_I"
                statusFloodClearSysLockIgnInput.status3.setBackgroundColor(if(it.ignIBit > 0) Color.GREEN else Color.LTGRAY)

                statusCondEpasAfterstrEnr.status1.text = "Вход COND_I"
                statusCondEpasAfterstrEnr.status1.setBackgroundColor(if(it.condIBit > 0) Color.GREEN else Color.LTGRAY)
                statusCondEpasAfterstrEnr.status2.text = "Вход EPAS_I"
                statusCondEpasAfterstrEnr.status2.setBackgroundColor(if(it.epasIBit > 0) Color.GREEN else Color.LTGRAY)
                statusCondEpasAfterstrEnr.status3.text = "Обог. после пуска"
                statusCondEpasAfterstrEnr.status3.setBackgroundColor(if(it.afterStEnrBit > 0) Color.GREEN else Color.LTGRAY)

                statusClosedLoopReservReserv.status1.text = "РХХ closed loop"
                statusClosedLoopReservReserv.status1.setBackgroundColor(if(it.iacClosedLoopBit > 0) Color.GREEN else Color.LTGRAY)

                statusUni1Uni2Uni3.status1.text = "Универ. 1"
                statusUni1Uni2Uni3.status1.setBackgroundColor(if(it.uniOut0Bit > 0) Color.GREEN else Color.LTGRAY)
                statusUni1Uni2Uni3.status2.text = "Универ. 2"
                statusUni1Uni2Uni3.status2.setBackgroundColor(if(it.uniOut1Bit > 0) Color.GREEN else Color.LTGRAY)
                statusUni1Uni2Uni3.status3.text = "Универ. 3"
                statusUni1Uni2Uni3.status3.setBackgroundColor(if(it.uniOut2Bit > 0) Color.GREEN else Color.LTGRAY)

                statusUni4Uni5Uni6.status1.text = "Универ. 4"
                statusUni4Uni5Uni6.status1.setBackgroundColor(if(it.uniOut3Bit > 0) Color.GREEN else Color.LTGRAY)
                statusUni4Uni5Uni6.status2.text = "Универ. 5"
                statusUni4Uni5Uni6.status2.setBackgroundColor(if(it.uniOut4Bit > 0) Color.GREEN else Color.LTGRAY)
                statusUni4Uni5Uni6.status3.text = "Универ. 6"
                statusUni4Uni5Uni6.status3.setBackgroundColor(if(it.uniOut5Bit > 0) Color.GREEN else Color.LTGRAY)
            }
        }

        /*mViewModel.rawSensorsLiveData.observe(viewLifecycleOwner) {

            var result = ""
            result += String.format(Locale.US, getString(R.string.raw_status_map_title), it.map)
            result += String.format(Locale.US, getString(R.string.raw_status_voltage_title), it.voltage)
            result += String.format(Locale.US, getString(R.string.raw_status_temperature_title), it.temperature)
            result += String.format(Locale.US, getString(R.string.raw_status_knock_title), it.knockValue)
            result += String.format(Locale.US, getString(R.string.raw_status_tps_title), it.tps)
            result += String.format(Locale.US, getString(R.string.raw_status_addi1_title), it.addI1)
            result += String.format(Locale.US, getString(R.string.raw_status_addi2_title), it.addI2)
            result += String.format(Locale.US, getString(R.string.raw_status_addi3_title), it.addI3)
            result += String.format(Locale.US, getString(R.string.raw_status_addi4_title), it.addI4)
            result += String.format(Locale.US, getString(R.string.raw_status_addi5_title), it.addI5)
            result += String.format(Locale.US, getString(R.string.raw_status_addi6_title), it.addI6)
            result += String.format(Locale.US, getString(R.string.raw_status_addi7_title), it.addI7)
            result += String.format(Locale.US, getString(R.string.raw_status_addi8_title), it.addI8)

        }*/
    }

    override fun onStart() {
        super.onStart()
        mViewModel.sendNewTask(Task.Secu3ReadFirmwareInfo)
    }

    private fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_dashboard -> {
                findNavController().navigate(R.id.action_sensors_to_dashboard)
                true
            }
            R.id.menu_preferences -> {
                startActivity(Intent(context, SettingsActivity::class.java))
                true
            }
            R.id.menu_params -> {
                findNavController().navigate(R.id.action_sensors_to_parameters)
                true
            }
            R.id.menu_errors -> {
                findNavController().navigate(R.id.action_sensors_to_errors)
                true
            }
            R.id.menu_exit -> {
                exit()
                true
            }
            R.id.menu_diagnostics -> {
                MaterialAlertDialogBuilder(requireContext()).setTitle(android.R.string.dialog_alert_title)
                    .setIcon(android.R.drawable.ic_dialog_alert).setMessage(R.string.menu_diagnostics_warning_title)
                    .setPositiveButton(android.R.string.ok) { _, _ ->

                        if (mViewModel.firmware?.isDiagnosticsEnabled == true) {
                            findNavController().navigate(R.id.action_sensors_to_diagnostics)
                            return@setPositiveButton
                        }

                        Toast.makeText(context, R.string.diagnostics_not_supported_title, Toast.LENGTH_LONG).show()

                    }.setNegativeButton(android.R.string.cancel, null).create().show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun exit() {
        mViewModel.closeConnection()
        activity?.finishAndRemoveTask()
    }
}