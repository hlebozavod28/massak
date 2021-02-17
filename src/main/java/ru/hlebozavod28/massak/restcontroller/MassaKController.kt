package ru.hlebozavod28.massak.restcontroller

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import kotlin.Throws
import java.lang.InterruptedException
import com.jacob.activeX.ActiveXComponent
import com.jacob.com.Dispatch
import com.jacob.com.Variant
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import ru.hlebozavod28.massak.DAO.*
import ru.hlebozavod28.massak.domain.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

@RestController
class MassaKController(
    private val workplaceCrudRepository: WorkplaceCrudRepository,
    private val weightingCrudRepository: WeightingCrudRepository,
    private val motionJpa: MotionJpa,
    private val handcartSheetsJpa: HandcartSheetsJpa,
    private val productCrudRepository: ProductCrudRepository
) {
    @Value("\${hlebozavod28.handcartdefaultsheets}")
    private val defaultSheetsStr: String? = null

    private val log = Logger.getLogger(MassaKController::class.java.name)

    private var retCode = 0

    @GetMapping("/inhandcart")
    @Throws(InterruptedException::class)
    private fun inHandCart( workplace: Long, handcart: Long, productcode: Int): String {
        retCode = 0
        workplaceCrudRepository.findById(workplace)?.apply {
            log.info("New hand cart in workplace $workPlaceName")

            // cart motion
            motionJpa.findByWorkplaceIdAndAmountNullAndDeletedFalse(workplace).forEach {
                it.deleted = true
                log.info("delete wrong motion id=" + it.id)
                motionJpa.save(it)
            }
            val sheets = (handcartSheetsJpa.getByHandcart(handcart)?: HandcartSheets(defaultSheetsStr!!.toInt())).sheets
            motionJpa.save( Motion(workplace, handcart, productcode, sheets) )
            // weighting
            for (sc in scales) {
                log.info(" scale=" + sc.ipaddr)
                // find old non completed
                weightingCrudRepository.findWeightings(sc.id, id).forEach {
                    it.deleted = true
                    weightingCrudRepository.save(it)
                    log.info(" deleting empty weighting")
                }
                getWeight(sc.ipaddr)?.let { weightingCrudRepository.save(Weighting(it, sc.id, id)) }
            }
        } ?: throw NoRecordFindException()
        return retCode.toString()
    }

    private fun getWeight(ipaddr: String?): Int? {
        var weight: Int?  = null
        val scale = ActiveXComponent("MassaKDriver100.Scales")
        Dispatch.put(scale, "Connection", Variant(ipaddr))
        val oc = Dispatch.call(scale, "OpenConnection")
        if (oc.int == 0) {
            Dispatch.call(scale, "ReadWeight")
            val st = Dispatch.get(scale, "Stable").int
            if (st == 0) {
                log.info("  no stable, wait")
                TimeUnit.SECONDS.sleep(2)
                retCode = 100
            }
            weight = Dispatch.get(scale, "Weight").int
            log.info("  weight=$weight")
        } else {
            log.severe("cant get data from scale " + ipaddr + " error=" + oc.int)
            retCode = oc.int
        }
        Dispatch.call(scale, "CloseConnection")
        return weight
    }

    @GetMapping("/outhandcart")
    @Throws(InterruptedException::class)
    private fun outHandCart(workplace: Long, handcart: Long): ResponseEntity<String> {
        retCode = 0
        workplaceCrudRepository.findById(workplace)?.apply {
            log.info("Out handcart from workolace $workPlaceName")

            // write to motion
            val newMotions = motionJpa.findHandCart(workplace, handcart)
            for (motion in newMotions) {
                motion.outTs = Timestamp(System.currentTimeMillis())
                var defectWeight = 0
                for (sc in scales) {
                    log.info(" scale=" + sc.ipaddr)
                    getWeight(sc.ipaddr)?.apply {
                        weightingCrudRepository.findWeightings(sc.id, id).forEach {
                            it.finalWeight = this
                            it.completed = true
                            weightingCrudRepository.save(it)
                            defectWeight += it.finalWeight - it.initialWeight
                        }
                    }
                }
                // calculate amount
                val product =
                    productCrudRepository.findById(motion.productCode.toLong()).orElseThrow { NoRecordFindException() }
                var defectCount = BigDecimal(defectWeight)
                log.info("defect weight=$defectCount")
                val oneWeight = product.weightdough.multiply(BigDecimal(1000))
                log.info("one product weight=$oneWeight")
                defectCount = defectCount.divide(oneWeight, 0, RoundingMode.HALF_UP)
                log.info("defect count=$defectCount")
                motion.defectCount = defectCount
                recalcMotion(motion)
            }
        } ?: throw NoRecordFindException()
        return ResponseEntity.ok(retCode.toString())
    }

    @GetMapping("/deletehadcart")
    private fun deleteHandCart(workplace: Long, handcart: Long): String {
        // delete from weighting
        workplaceCrudRepository.findById(workplace)?.apply {
            log.info("Delete handcart from weighting $workPlaceName")
            for (sc in scales) {
                // find old weighting
                weightingCrudRepository.findWeightings(sc.id, id).forEach {
                    it.deleted = true
                    weightingCrudRepository.save(it)
                    log.info(" scale=" + sc.ipaddr)
                }
            }
            // delete from motions
            log.info("delete handcart from motion workplace= $workplace handcart=$handcart")
            val motion2del = if (handcart == 0L) {
                motionJpa.findFirstByWorkplaceIdAndDeletedFalseOrderByIdDesc(workplace)
            } else {
                motionJpa.findFirstByWorkplaceIdAndHandcartIdAndDeletedFalseOrderByIdDesc(workplace, handcart)
            } ?: throw NoRecordFindException()
            motion2del.deleted = true
            motionJpa.save(motion2del)
        } ?: throw NoRecordFindException()
        return "0"
    }

    @GetMapping("/changehadcart")
    private fun changeHandCart(workplace: Long, handcart: Long, sheets: Int): String {
        val motionChange =
            motionJpa.findFirstByWorkplaceIdAndHandcartIdAndDeletedFalseOrderByIdDesc(workplace, handcart)
                ?: throw NoRecordFindException()
        log.info("change sheet for workplace $workplace and handcart $handcart to $sheets (id=${motionChange.id})")
        motionChange.sheets = sheets
        motionChange.amount?.let {
            motionJpa.save(motionChange)
        } ?: run {
            recalcMotion(motionChange)
        }
        return "0"
    }

    private fun recalcMotion(motion: Motion) {
        val product =
            productCrudRepository.findById(motion.productCode.toLong()).orElseThrow { NoRecordFindException() }
        val prodOfSheet = BigDecimal(product.sheetalloc)
        val sheets = BigDecimal(motion.sheets)
        val defectCount = motion.defectCount
        val amount = prodOfSheet.multiply(sheets).subtract(defectCount)
        motion.amount = amount
        log.info("amount=$sheets * $prodOfSheet - $defectCount = $amount")
        motionJpa.save(motion)
    }
}