package ua.lpnu.shuliak.lab1.service

class SyncPerfectNumberService : AbstractPerfectNumberService() {
    override fun findPerfectNumbers(limit: Int): List<Int> {
        val perfectNumbers = mutableListOf<Int>()
        var num = 2
        while (perfectNumbers.size < limit) {
            if (isPerfectNumber(num)) {
                perfectNumbers.add(num)
            }
            num++
        }
        return perfectNumbers
    }
}