# 🌱 Carbon Footprint Calculator — DAA Edition

A console-based Java application that calculates your annual carbon footprint across three lifestyle categories and uses **four classic DAA algorithms** to analyze and optimize your eco-action plan.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Algorithms Used](#algorithms-used)
- [How to Run](#how-to-run)
- [Sample Input & Output](#sample-input--output)
- [Emission Factors](#emission-factors)
- [Classes & Responsibilities](#classes--responsibilities)

---

## Overview

This program takes three inputs from the user:
- **Transport mode** and weekly distance
- **Monthly electricity usage** (kWh)
- **Diet type**

It then computes the total annual CO₂ emission (in kg) and applies four DAA algorithms to rank, classify, and suggest reduction strategies.

---

## Features

- ✅ Calculates annual CO₂ from transport, electricity, and food
- ✅ Ranks emission sources using **Merge Sort**
- ✅ Classifies emission level using **Binary Search**
- ✅ Suggests eco-actions by highest saving-to-effort ratio using a **Greedy Algorithm**
- ✅ Finds the optimal action combination within a budget using **0/1 Knapsack DP**
- ✅ Compares result against the global average (4,000 kg CO₂/year)

---

## Project Structure

```
CarbonFootprintApp.java       ← Single-file Java application
│
├── Enums
│   ├── TransportType         (CAR, BUS, TRAIN, BICYCLE, WALKING)
│   └── DietType              (HEAVY_MEAT, AVERAGE_MEAT, VEGETARIAN, VEGAN)
│
├── Emission Source Classes
│   ├── Transport
│   ├── ElectricityUsage
│   └── FoodConsumption
│
├── DAA Algorithm Classes
│   ├── MergeSort             → Ranks categories by emission
│   ├── BinarySearch          → Classifies emission level
│   ├── GreedyReductionPlanner → Selects best actions by CO₂/effort ratio
│   └── DPKnapsackPlanner     → Finds optimal actions via 0/1 Knapsack
│
├── CarbonFootprintCalculator ← Aggregates emissions
└── CarbonFootprintApp        ← Main class (entry point)
```

---

## Algorithms Used

### 1. Merge Sort — `O(n log n)`
**Purpose:** Ranks the three emission categories (Transport, Electricity, Food) from highest to lowest.

The parallel arrays of emission values and their labels are sorted together in **descending order**, so the user immediately sees their worst-contributing category.

---

### 2. Binary Search — `O(log n)`
**Purpose:** Looks up the user's emission level from a sorted threshold table.

| Level    | Up to (kg CO₂/year) |
|----------|----------------------|
| LOW      | 1,000                |
| MEDIUM   | 3,000                |
| HIGH     | 6,000                |
| CRITICAL | 10,000               |
| EXTREME  | > 10,000             |

Binary search finds the correct bracket in `O(log n)` time instead of scanning linearly.

---

### 3. Greedy Algorithm — `O(n log n)`
**Purpose:** Selects eco-actions that maximize CO₂ savings within an **effort budget of 10 units**.

Each action has:
- `co2Saving` — kg of CO₂ saved per year
- `effortScore` — difficulty (1 = easy, 10 = hard)
- `ratio = co2Saving / effortScore`

Actions are sorted by ratio (descending) and greedily picked until the budget runs out — a **Fractional Knapsack** style approach applied to integer effort units.

---

### 4. Dynamic Programming — `O(n × W)`
**Purpose:** Finds the **optimal combination** of actions that maximizes total CO₂ saving within an effort budget of **8 units**.

This is the classic **0/1 Knapsack** problem — each action is either fully taken or not. A 2D DP table is filled and backtracked to identify selected items.

| | Greedy | DP Knapsack |
|---|---|---|
| Approach | Ratio-based selection | Exhaustive optimal |
| Budget | 10 units | 8 units |
| Optimality | Near-optimal | Guaranteed optimal |
| Complexity | O(n log n) | O(n × W) |

---

## How to Run

### Requirements
- Java 8 or higher
- No external libraries needed

### Compile

```bash
javac CarbonFootprintApp.java
```

### Run

```bash
java CarbonFootprintApp
```

### Input Prompts

```
Your primary transport mode: CAR
Weekly distance travelled (km): 150
Monthly electricity usage (kWh): 300
Your diet type: AVERAGE_MEAT
```

---

## Sample Input & Output

**Input:**
```
Transport: CAR, 150 km/week
Electricity: 300 kWh/month
Diet: AVERAGE_MEAT
```

**Output (summary):**
```
#1  Food/Diet      :  2500.00 kg CO2/year
#2  Transport      :  1638.00 kg CO2/year
#3  Electricity    :  1800.00 kg CO2/year

► TOTAL ANNUAL FOOTPRINT : 5938.00 kg CO2  (5.938 tonnes)
► YOUR LEVEL : [ HIGH ]

⚠  You are ABOVE the global average. Follow the plan above.
```

---

## Emission Factors

| Source         | Factor                              |
|----------------|-------------------------------------|
| Car            | 0.21 kg CO₂ per km                  |
| Bus            | 0.08 kg CO₂ per km                  |
| Train          | 0.04 kg CO₂ per km                  |
| Bicycle/Walk   | 0 kg CO₂                            |
| Electricity    | 0.5 kg CO₂ per kWh                  |
| Heavy Meat diet| 3,300 kg CO₂/year                   |
| Average Meat   | 2,500 kg CO₂/year                   |
| Vegetarian     | 1,700 kg CO₂/year                   |
| Vegan          | 1,500 kg CO₂/year                   |

---

## Classes & Responsibilities

| Class | Role |
|---|---|
| `Transport` | Stores transport type + weekly km; computes annual emission |
| `ElectricityUsage` | Stores monthly kWh; computes annual emission |
| `FoodConsumption` | Stores diet type; returns fixed annual emission |
| `MergeSort` | Sorts parallel value+label arrays in descending order |
| `BinarySearch` | Maps total emission to a named level via threshold lookup |
| `GreedyReductionPlanner` | Builds action list; picks by CO₂/effort ratio greedily |
| `DPKnapsackPlanner` | Solves 0/1 knapsack on action set; backtracks for selected items |
| `CarbonFootprintCalculator` | Aggregates the three emission sources |
| `CarbonFootprintApp` | Entry point; handles I/O and orchestrates all algorithms |

---

## Global Context

> 🌍 The global average carbon footprint is approximately **4,000 kg CO₂ per person per year**.  
> The recommended target to limit climate change is under **2,000 kg CO₂/year**.

---

*Built as a Design and Analysis of Algorithms (DAA) project demonstrating real-world applications of Merge Sort, Binary Search, Greedy, and Dynamic Programming.*

---

## Sample Console Output

```
╔══════════════════════════════════════════════╗
║   CARBON FOOTPRINT CALCULATOR  (DAA Edition) ║
╚══════════════════════════════════════════════╝

╔══════════════════════════════════════════════╗
║  STEP 1 — Transport                          ║
╚══════════════════════════════════════════════╝
Options: CAR  BUS  TRAIN  BICYCLE  WALKING
Your primary transport mode: BUS
Weekly distance travelled (km): 20

╔══════════════════════════════════════════════╗
║  STEP 2 — Electricity                        ║
╚══════════════════════════════════════════════╝
Monthly electricity usage (kWh): 40

╔══════════════════════════════════════════════╗
║  STEP 3 — Food / Diet                        ║
╚══════════════════════════════════════════════╝
Options: HEAVY_MEAT  AVERAGE_MEAT  VEGETARIAN  VEGAN
Your diet type: VEGETARIAN

╔══════════════════════════════════════════════╗
║  RESULTS — Emission Breakdown (Merge Sort Ranked)║
╚══════════════════════════════════════════════╝
  [Algorithm: Merge Sort — O(n log n)]
  Ranking your emission sources from highest to lowest...

  #1  Food/Diet      :  1700.00 kg CO2/year
  #2  Electricity    :   240.00 kg CO2/year
  #3  Transport      :    83.20 kg CO2/year

  ► TOTAL ANNUAL FOOTPRINT : 2023.20 kg CO2  (2.023 tonnes)

╔══════════════════════════════════════════════╗
║  EMISSION LEVEL ASSESSMENT (Binary Search)   ║
╚══════════════════════════════════════════════╝
  [Algorithm: Binary Search — O(log n)]
  Looking up your level in the threshold table...

  Threshold Table (kg CO2/year):
  ┌──────────┬────────────────┐
  │  Level   │   Up to (kg)   │
  ├──────────┼────────────────┤
  │ LOW      │      1,000     │
  │ MEDIUM   │      3,000     │
  │ HIGH     │      6,000     │
  │ CRITICAL │     10,000     │
  │ EXTREME  │     > 10,000   │
  └──────────┴────────────────┘

  ► YOUR LEVEL : [ MEDIUM ]  (2023 kg CO2)

╔══════════════════════════════════════════════╗
║  ECO-ACTION PLAN — Greedy Algorithm          ║
╚══════════════════════════════════════════════╝
  [Algorithm: Greedy (Fractional Knapsack) — O(n log n)]
  Maximising CO2 savings within your effort budget...
  (Effort budget = 10 units  |  Sorted by CO2-saved/effort ratio)

  Great job! Your footprint is already very low.

╔══════════════════════════════════════════════╗
║  OPTIMAL ACTION PLAN — Dynamic Programming (0/1 Knapsack)║
╚══════════════════════════════════════════════╝
  [Algorithm: 0/1 Knapsack DP — O(n × W)]
  Finding the OPTIMAL combination of actions (effort budget = 8)...

  No actions needed — your footprint is already low!

╔══════════════════════════════════════════════╗
║  FINAL SUMMARY                               ║
╚══════════════════════════════════════════════╝
  Total Footprint     : 2023.20 kg CO2/year
  Emission Level      : MEDIUM
  Worst Category      : Food/Diet
  Best Category       : Transport

  Global average is ~4,000 kg CO2/person/year.
  ✅ You are BELOW the global average. Well done!
------------------------------------------------
  Thank you for calculating your carbon footprint!

Process finished with exit code 0
```

> **Input used:** Transport = BUS (20 km/week), Electricity = 40 kWh/month, Diet = VEGETARIAN
