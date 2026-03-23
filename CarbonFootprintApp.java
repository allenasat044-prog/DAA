import java.util.*;

// ─────────────────────────────────────────────
//  ENUMS
// ─────────────────────────────────────────────
enum TransportType {
    CAR, BUS, TRAIN, BICYCLE, WALKING
}

enum DietType {
    HEAVY_MEAT, AVERAGE_MEAT, VEGETARIAN, VEGAN
}

// ─────────────────────────────────────────────
//  EMISSION SOURCE CLASSES
// ─────────────────────────────────────────────
class Transport {
    private TransportType type;
    private double weeklyDistanceKm;

    public Transport(TransportType type, double weeklyDistanceKm) {
        this.type = type;
        this.weeklyDistanceKm = weeklyDistanceKm;
    }

    public TransportType getType() { return type; }

    public double getAnnualEmission() {
        double annualDistance = weeklyDistanceKm * 52;
        switch (type) {
            case CAR:     return annualDistance * 0.21;
            case BUS:     return annualDistance * 0.08;
            case TRAIN:   return annualDistance * 0.04;
            case BICYCLE:
            case WALKING: return 0;
            default:      return 0;
        }
    }
}

class ElectricityUsage {
    private double monthlyKwh;

    public ElectricityUsage(double monthlyKwh) {
        this.monthlyKwh = monthlyKwh;
    }

    public double getAnnualEmission() {
        return (monthlyKwh * 12) * 0.5; // 0.5 kg CO2 per kWh
    }
}

class FoodConsumption {
    private DietType diet;

    public FoodConsumption(DietType diet) {
        this.diet = diet;
    }

    public DietType getDiet() { return diet; }

    public double getAnnualEmission() {
        switch (diet) {
            case HEAVY_MEAT:  return 3300;
            case AVERAGE_MEAT: return 2500;
            case VEGETARIAN:  return 1700;
            case VEGAN:       return 1500;
            default:          return 0;
        }
    }
}

// ─────────────────────────────────────────────
//  DAA ALGORITHM 1: MERGE SORT
//  Used to rank emission categories from highest to lowest
//  so user sees the worst offender first.
// ─────────────────────────────────────────────
class MergeSort {
    // Sort category entries descending by emission value
    public static void sort(double[] values, String[] labels) {
        mergeSort(values, labels, 0, values.length - 1);
    }

    private static void mergeSort(double[] v, String[] l, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(v, l, left, mid);
        mergeSort(v, l, mid + 1, right);
        merge(v, l, left, mid, right);
    }

    private static void merge(double[] v, String[] l, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        double[] lv = new double[n1]; String[] ll = new String[n1];
        double[] rv = new double[n2]; String[] rl = new String[n2];

        System.arraycopy(v, left,    lv, 0, n1);
        System.arraycopy(l, left,    ll, 0, n1);
        System.arraycopy(v, mid + 1, rv, 0, n2);
        System.arraycopy(l, mid + 1, rl, 0, n2);

        int i = 0, j = 0, k = left;
        // Descending order
        while (i < n1 && j < n2) {
            if (lv[i] >= rv[j]) { v[k] = lv[i]; l[k] = ll[i]; i++; }
            else                { v[k] = rv[j]; l[k] = rl[j]; j++; }
            k++;
        }
        while (i < n1) { v[k] = lv[i]; l[k] = ll[i]; i++; k++; }
        while (j < n2) { v[k] = rv[j]; l[k] = rl[j]; j++; k++; }
    }
}

// ─────────────────────────────────────────────
//  DAA ALGORITHM 2: BINARY SEARCH
//  Used to look up the emission level category (LOW/MEDIUM/HIGH/CRITICAL)
//  from a sorted threshold table in O(log n) time.
// ─────────────────────────────────────────────
class BinarySearch {
    // Thresholds in kg CO2 (sorted ascending)
    private static final double[] THRESHOLDS = {1000, 3000, 6000, 10000, Double.MAX_VALUE};
    private static final String[] LEVELS     = {"LOW", "MEDIUM", "HIGH", "CRITICAL", "EXTREME"};

    public static String findLevel(double totalEmission) {
        int lo = 0, hi = THRESHOLDS.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (totalEmission <= THRESHOLDS[mid]) {
                // Check if also greater than previous threshold
                if (mid == 0 || totalEmission > THRESHOLDS[mid - 1])
                    return LEVELS[mid];
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }
        return LEVELS[LEVELS.length - 1];
    }
}

// ─────────────────────────────────────────────
//  DAA ALGORITHM 3: GREEDY ALGORITHM (Fractional Knapsack variant)
//  Used to select the TOP reduction actions within a given
//  "effort budget" to maximise CO2 savings.
//  Each action has: co2Saving (value), effortScore (weight).
//  Greedy picks highest saving/effort ratio first.
// ─────────────────────────────────────────────
class GreedyReductionPlanner {

    static class Action {
        String description;
        double co2Saving;   // kg CO2 saved per year
        double effortScore; // 1 = easy, 10 = hard
        double ratio;

        Action(String desc, double saving, double effort) {
            this.description = desc;
            this.co2Saving   = saving;
            this.effortScore = effort;
            this.ratio       = saving / effort;
        }
    }

    private List<Action> allActions = new ArrayList<>();

    public GreedyReductionPlanner(double transportEmission,
                                  double electricityEmission,
                                  double foodEmission) {
        // Transport actions
        if (transportEmission > 500) {
            allActions.add(new Action("Switch from CAR to BUS for daily commute",
                    transportEmission * 0.60, 4));
            allActions.add(new Action("Carpool with 3 others (cut car emissions by 75%)",
                    transportEmission * 0.75, 3));
            allActions.add(new Action("Use bicycle for trips under 5 km",
                    transportEmission * 0.30, 2));
            allActions.add(new Action("Switch to electric vehicle",
                    transportEmission * 0.80, 9));
        }
        // Electricity actions
        if (electricityEmission > 500) {
            allActions.add(new Action("Switch all bulbs to LED",
                    electricityEmission * 0.10, 1));
            allActions.add(new Action("Install solar panels",
                    electricityEmission * 0.70, 10));
            allActions.add(new Action("Unplug standby devices daily",
                    electricityEmission * 0.05, 1));
            allActions.add(new Action("Upgrade to 5-star rated appliances",
                    electricityEmission * 0.25, 5));
        }
        // Food actions
        if (foodEmission > 2000) {
            allActions.add(new Action("Adopt Meatless Mondays (cut meat 1 day/week)",
                    foodEmission * 0.14, 1));
            allActions.add(new Action("Shift to vegetarian diet",
                    foodEmission * 0.30, 4));
            allActions.add(new Action("Shift to vegan diet",
                    foodEmission * 0.45, 6));
            allActions.add(new Action("Reduce food waste by 50%",
                    foodEmission * 0.10, 2));
        }
    }

    /**
     * Greedy selection: sort by CO2/effort ratio (descending),
     * pick actions until effortBudget is exhausted.
     */
    public List<Action> getTopActions(double effortBudget) {
        // Sort by ratio descending (Greedy criterion)
        allActions.sort((a, b) -> Double.compare(b.ratio, a.ratio));

        List<Action> selected = new ArrayList<>();
        double remainingBudget = effortBudget;

        for (Action a : allActions) {
            if (a.effortScore <= remainingBudget) {
                selected.add(a);
                remainingBudget -= a.effortScore;
            }
        }
        return selected;
    }
}

// ─────────────────────────────────────────────
//  DAA ALGORITHM 4: DYNAMIC PROGRAMMING
//  0/1 Knapsack: Given a fixed "lifestyle change budget" (int units),
//  find the combination of actions that gives MAXIMUM CO2 saving
//  without exceeding budget. Each action is either taken or not (0/1).
// ─────────────────────────────────────────────
class DPKnapsackPlanner {

    static class Action {
        String description;
        int co2Saving;   // rounded kg
        int effortCost;  // integer effort units

        Action(String desc, int saving, int cost) {
            this.description = desc;
            this.co2Saving   = saving;
            this.effortCost  = cost;
        }
    }

    private List<Action> actions = new ArrayList<>();

    public DPKnapsackPlanner(double transportEmission,
                             double electricityEmission,
                             double foodEmission) {
        if (transportEmission > 500) {
            actions.add(new Action("Switch to public transport",
                    (int)(transportEmission * 0.60), 4));
            actions.add(new Action("Carpool daily",
                    (int)(transportEmission * 0.75), 3));
            actions.add(new Action("Use bicycle for short trips",
                    (int)(transportEmission * 0.30), 2));
        }
        if (electricityEmission > 500) {
            actions.add(new Action("Switch to LED bulbs",
                    (int)(electricityEmission * 0.10), 1));
            actions.add(new Action("Install solar panels",
                    (int)(electricityEmission * 0.70), 8));
            actions.add(new Action("Use energy-efficient appliances",
                    (int)(electricityEmission * 0.25), 4));
        }
        if (foodEmission > 2000) {
            actions.add(new Action("Meatless Mondays",
                    (int)(foodEmission * 0.14), 1));
            actions.add(new Action("Shift to vegetarian diet",
                    (int)(foodEmission * 0.30), 3));
            actions.add(new Action("Reduce food waste",
                    (int)(foodEmission * 0.10), 1));
        }
    }

    /**
     * 0/1 Knapsack DP.
     * Returns the selected actions that maximise CO2 saving within budget.
     */
    public List<Action> solve(int budget) {
        int n = actions.size();
        int[][] dp = new int[n + 1][budget + 1];

        // Fill DP table
        for (int i = 1; i <= n; i++) {
            Action act = actions.get(i - 1);
            for (int w = 0; w <= budget; w++) {
                dp[i][w] = dp[i - 1][w]; // don't take
                if (act.effortCost <= w) {
                    dp[i][w] = Math.max(dp[i][w],
                            dp[i - 1][w - act.effortCost] + act.co2Saving);
                }
            }
        }

        // Backtrack to find selected items
        List<Action> selected = new ArrayList<>();
        int w = budget;
        for (int i = n; i >= 1; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                selected.add(actions.get(i - 1));
                w -= actions.get(i - 1).effortCost;
            }
        }
        return selected;
    }
}

// ─────────────────────────────────────────────
//  MAIN CALCULATOR CLASS
// ─────────────────────────────────────────────
class CarbonFootprintCalculator {
    private Transport transport;
    private ElectricityUsage electricity;
    private FoodConsumption food;

    public CarbonFootprintCalculator(Transport t, ElectricityUsage e, FoodConsumption f) {
        this.transport   = t;
        this.electricity = e;
        this.food        = f;
    }

    public double calculateTotalFootprint() {
        return transport.getAnnualEmission()
                + electricity.getAnnualEmission()
                + food.getAnnualEmission();
    }

    public double getTransportEmission()    { return transport.getAnnualEmission(); }
    public double getElectricityEmission()  { return electricity.getAnnualEmission(); }
    public double getFoodEmission()         { return food.getAnnualEmission(); }
}

// ─────────────────────────────────────────────
//  MAIN APP
// ─────────────────────────────────────────────
public class CarbonFootprintApp {

    // ── Helper: print a section header ──
    static void header(String title) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.printf ("║  %-44s║%n", title);
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    static void line() {
        System.out.println("------------------------------------------------");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   CARBON FOOTPRINT CALCULATOR  (DAA Edition) ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        // ── INPUT: Transport ──
        header("STEP 1 — Transport");
        System.out.println("Options: CAR  BUS  TRAIN  BICYCLE  WALKING");
        System.out.print("Your primary transport mode: ");
        TransportType tType = TransportType.valueOf(sc.nextLine().trim().toUpperCase());
        System.out.print("Weekly distance travelled (km): ");
        double weeklyKm = Double.parseDouble(sc.nextLine().trim());
        Transport transport = new Transport(tType, weeklyKm);

        // ── INPUT: Electricity ──
        header("STEP 2 — Electricity");
        System.out.print("Monthly electricity usage (kWh): ");
        double monthlyKwh = Double.parseDouble(sc.nextLine().trim());
        ElectricityUsage electricity = new ElectricityUsage(monthlyKwh);

        // ── INPUT: Food ──
        header("STEP 3 — Food / Diet");
        System.out.println("Options: HEAVY_MEAT  AVERAGE_MEAT  VEGETARIAN  VEGAN");
        System.out.print("Your diet type: ");
        DietType dType = DietType.valueOf(sc.nextLine().trim().toUpperCase());
        FoodConsumption food = new FoodConsumption(dType);

        // ── CALCULATE ──
        CarbonFootprintCalculator calc =
                new CarbonFootprintCalculator(transport, electricity, food);
        double tEmit = calc.getTransportEmission();
        double eEmit = calc.getElectricityEmission();
        double fEmit = calc.getFoodEmission();
        double total = calc.calculateTotalFootprint();

        // ══════════════════════════════════════════
        //  ALGORITHM 1: MERGE SORT — Rank Categories
        // ══════════════════════════════════════════
        header("RESULTS — Emission Breakdown (Merge Sort Ranked)");
        System.out.println("  [Algorithm: Merge Sort — O(n log n)]");
        System.out.println("  Ranking your emission sources from highest to lowest...\n");

        double[] emissions = { tEmit, eEmit, fEmit };
        String[] labels    = { "Transport   ", "Electricity ", "Food/Diet   " };
        MergeSort.sort(emissions, labels);

        for (int i = 0; i < emissions.length; i++) {
            System.out.printf("  #%d  %-14s : %8.2f kg CO2/year%n", i + 1, labels[i], emissions[i]);
        }
        System.out.printf("%n  ► TOTAL ANNUAL FOOTPRINT : %.2f kg CO2  (%.3f tonnes)%n",
                total, total / 1000.0);

        // ══════════════════════════════════════════
        //  ALGORITHM 2: BINARY SEARCH — Emission Level
        // ══════════════════════════════════════════
        header("EMISSION LEVEL ASSESSMENT (Binary Search)");
        System.out.println("  [Algorithm: Binary Search — O(log n)]");
        System.out.println("  Looking up your level in the threshold table...\n");

        String level = BinarySearch.findLevel(total);
        System.out.println("  Threshold Table (kg CO2/year):");
        System.out.println("  ┌──────────┬────────────────┐");
        System.out.println("  │  Level   │   Up to (kg)   │");
        System.out.println("  ├──────────┼────────────────┤");
        System.out.println("  │ LOW      │      1,000     │");
        System.out.println("  │ MEDIUM   │      3,000     │");
        System.out.println("  │ HIGH     │      6,000     │");
        System.out.println("  │ CRITICAL │     10,000     │");
        System.out.println("  │ EXTREME  │     > 10,000   │");
        System.out.println("  └──────────┴────────────────┘");
        System.out.printf("%n  ► YOUR LEVEL : [ %s ]  (%.0f kg CO2)%n", level, total);

        // ══════════════════════════════════════════
        //  ALGORITHM 3: GREEDY — Best Actions by Ratio
        // ══════════════════════════════════════════
        header("ECO-ACTION PLAN — Greedy Algorithm");
        System.out.println("  [Algorithm: Greedy (Fractional Knapsack) — O(n log n)]");
        System.out.println("  Maximising CO2 savings within your effort budget...");
        System.out.println("  (Effort budget = 10 units  |  Sorted by CO2-saved/effort ratio)\n");

        GreedyReductionPlanner greedy =
                new GreedyReductionPlanner(tEmit, eEmit, fEmit);
        List<GreedyReductionPlanner.Action> greedyPlan = greedy.getTopActions(10.0);

        if (greedyPlan.isEmpty()) {
            System.out.println("  Great job! Your footprint is already very low.");
        } else {
            double totalSaving = 0;
            for (GreedyReductionPlanner.Action a : greedyPlan) {
                System.out.printf("  ✔ %-45s  saves %6.0f kg  (effort: %.0f)%n",
                        a.description, a.co2Saving, a.effortScore);
                totalSaving += a.co2Saving;
            }
            System.out.printf("%n  ► TOTAL POTENTIAL SAVING : %.0f kg CO2/year (%.1f%% reduction)%n",
                    totalSaving, (totalSaving / total) * 100);
        }

        // ══════════════════════════════════════════
        //  ALGORITHM 4: DP KNAPSACK — Optimal Plan
        // ══════════════════════════════════════════
        header("OPTIMAL ACTION PLAN — Dynamic Programming (0/1 Knapsack)");
        System.out.println("  [Algorithm: 0/1 Knapsack DP — O(n × W)]");
        System.out.println("  Finding the OPTIMAL combination of actions (effort budget = 8)...\n");

        DPKnapsackPlanner dp = new DPKnapsackPlanner(tEmit, eEmit, fEmit);
        List<DPKnapsackPlanner.Action> dpPlan = dp.solve(8);

        if (dpPlan.isEmpty()) {
            System.out.println("  No actions needed — your footprint is already low!");
        } else {
            int dpTotalSaving = 0;
            for (DPKnapsackPlanner.Action a : dpPlan) {
                System.out.printf("  ✔ %-45s  saves %6d kg  (effort: %d)%n",
                        a.description, a.co2Saving, a.effortCost);
                dpTotalSaving += a.co2Saving;
            }
            System.out.printf("%n  ► MAXIMUM SAVING WITHIN BUDGET : %d kg CO2/year%n",
                    dpTotalSaving);
        }

        // ══════════════════════════════════════════
        //  FINAL SUMMARY
        // ══════════════════════════════════════════
        header("FINAL SUMMARY");
        System.out.printf("  Total Footprint     : %.2f kg CO2/year%n", total);
        System.out.printf("  Emission Level      : %s%n", level);
        System.out.printf("  Worst Category      : %s%n", labels[0].trim());
        System.out.printf("  Best Category       : %s%n", labels[2].trim());
        System.out.println("\n  Global average is ~4,000 kg CO2/person/year.");
        if (total < 4000)
            System.out.println("  ✅ You are BELOW the global average. Well done!");
        else
            System.out.println("  ⚠  You are ABOVE the global average. Follow the plan above.");

        line();
        System.out.println("  Thank you for calculating your carbon footprint!");

        sc.close();
    }
}