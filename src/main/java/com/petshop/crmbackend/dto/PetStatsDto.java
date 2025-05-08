package com.petshop.crmbackend.dto;

import java.util.Map;

public class PetStatsDto {

    /** 总宠物数 */
    private long totalPets;

    /** 今日新增宠物数 */
    private long todayNewPets;

    /** 本周新增宠物数 */
    private long weekNewPets;

    /** 本月新增宠物数 */
    private long monthNewPets;

    /** 按物种分布：species -> count */
    private Map<String, Long> speciesDistribution;

    /** 按品种分布：breedName -> count */
    private Map<String, Long> breedDistribution;

    /** 平均年龄（天） */
    private double averageAgeDays;

    public PetStatsDto() {
    }

    public long getTotalPets() {
        return totalPets;
    }

    public void setTotalPets(long totalPets) {
        this.totalPets = totalPets;
    }

    public long getTodayNewPets() {
        return todayNewPets;
    }

    public void setTodayNewPets(long todayNewPets) {
        this.todayNewPets = todayNewPets;
    }

    public long getWeekNewPets() {
        return weekNewPets;
    }

    public void setWeekNewPets(long weekNewPets) {
        this.weekNewPets = weekNewPets;
    }

    public long getMonthNewPets() {
        return monthNewPets;
    }

    public void setMonthNewPets(long monthNewPets) {
        this.monthNewPets = monthNewPets;
    }

    public Map<String, Long> getSpeciesDistribution() {
        return speciesDistribution;
    }

    public void setSpeciesDistribution(Map<String, Long> speciesDistribution) {
        this.speciesDistribution = speciesDistribution;
    }

    public Map<String, Long> getBreedDistribution() {
        return breedDistribution;
    }

    public void setBreedDistribution(Map<String, Long> breedDistribution) {
        this.breedDistribution = breedDistribution;
    }

    public double getAverageAgeDays() {
        return averageAgeDays;
    }

    public void setAverageAgeDays(double averageAgeDays) {
        this.averageAgeDays = averageAgeDays;
    }

    @Override
    public String toString() {
        return "PetStatsDto{" +
                "totalPets=" + totalPets +
                ", todayNewPets=" + todayNewPets +
                ", weekNewPets=" + weekNewPets +
                ", monthNewPets=" + monthNewPets +
                ", speciesDistribution=" + speciesDistribution +
                ", breedDistribution=" + breedDistribution +
                ", averageAgeDays=" + averageAgeDays +
                '}';
    }
}