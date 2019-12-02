package oktavia

class Pipeline {
    private val midStages: ArrayList<PipelineStage> = arrayListOf()
    private val sourceStages: ArrayList<SourceStage> = arrayListOf()
    private val targetStages: ArrayList<TargetStage> = arrayListOf()
    private var samplesRead: Int = 0

    fun <T: SourceStage> stage(stage: T): T {
        this.sourceStages.add(stage)
        return stage
    }

    fun <T: TargetStage> stage(stage: T): T {
        this.targetStages.add(stage)
        return stage
    }

    fun <T: PipelineStage> stage(stage: T): T {
        this.midStages.add(stage)
        return stage
    }

    fun run(numSamples: Int? = null): Int {
        val startingSampleRead = this.samplesRead
        while (true) {
            if (numSamples != null && this.samplesRead - startingSampleRead >= numSamples) {
                break
            }
            val success = this.updateStages()
            if (!success) {
                break
            }
            this.samplesRead++
        }
        return this.samplesRead - startingSampleRead
    }

    private fun updateStages(): Boolean {
        for (stage in this.sourceStages) {
            if (stage.halted()) {
                return false
            }
            stage.refreshData()
            stage.invalidateCache()
        }
        for (stage in this.midStages) {
            if (stage.halted()) {
                return false
            }
            stage.invalidateCache()
        }
        for (stage in this.targetStages) {
            if (stage.halted()) {
                return false
            }
            stage.invalidateCache()
        }
        for (stage in this.targetStages) {
            stage.pollNewData()
        }
        return true
    }
}