# ⚙️ Concurrent Linear Algebra Engine

**Multithreaded matrix computation with custom task scheduling**

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-Testing-25A162?style=for-the-badge&logo=junit5&logoColor=white)

**JSON Expression → Computation Tree → Concurrent Tasks → Worker Threads
→ Result Matrix**
:::

------------------------------------------------------------------------

A **Java-based concurrent linear algebra engine** that evaluates nested
matrix expressions using a custom thread pool, synchronized shared
memory, and fatigue-based task scheduling.

> 🎓 Academic project completed in a pair as part of the **SPL course at
> Ben-Gurion University**.

## Overview

The engine reads matrix expressions from JSON and supports:

-   `+` Matrix addition
-   `*` Matrix multiplication
-   `T` Transpose
-   `-` Negation

Operations can be nested to create more complex expressions. The input
is represented as a computation tree and evaluated progressively until
the final matrix is produced.

## How It Works

1.  **Parse** -- The JSON input is converted into a tree of
    `ComputationNode` objects.
2.  **Resolve** -- The engine finds operations whose operands are ready
    for computation.
3.  **Decompose** -- Matrix operations are split into vector/row-level
    `Runnable` tasks.
4.  **Execute** -- `TiredExecutor` distributes the tasks among worker
    threads.
5.  **Reduce** -- Results are written back into the computation tree
    until the final matrix is produced.

For operations with more than two operands, computation is evaluated
left-associatively.

## Concurrency & Scheduling

The engine uses a custom thread pool built around `TiredExecutor` and
`TiredThread`.

-   Matrix operations are divided into smaller tasks that can run
    concurrently.
-   Each worker tracks its execution time and has an associated
    **fatigue** value.
-   Tasks are preferentially assigned to less-fatigued idle workers to
    balance the workload.
-   Workers receive tasks through blocking handoff queues.
-   The executor waits for all tasks in the current batch before the
    engine continues.

Shared matrix data is represented using `SharedMatrix` and
`SharedVector`. `SharedVector` uses read/write locking to allow
concurrent reads while protecting writes.

## Input & Output

Input expressions are provided as JSON. For example, adding two
matrices:

``` json
{
  "operator": "+",
  "operands": [
    [
      [1, 2],
      [3, 4]
    ],
    [
      [5, 6],
      [7, 8]
    ]
  ]
}
```

The resulting output is:

``` json
{
  "result": [
    [6, 8],
    [10, 12]
  ]
}
```

Nested operations can be used as operands to construct more complex
expressions.

If an operation is invalid, the output contains an error instead:

``` json
{
  "error": "Illegal operation: dimensions mismatch"
}
```

## Running the Project

### Requirements

-   **Java 21** or a compatible version
-   **Maven**

### Build

From the project root:

``` bash
mvn package
```

This compiles the project, runs the tests, and creates the JAR file
under `target/`.

### Run

The program accepts three command-line arguments:

``` text
<number of threads> <input file> <output file>
```

Example:

``` bash
java -jar target/lga-1.0.jar 4 ./input.json ./output.json
```

Where:

-   `4` is the number of worker threads.
-   `./input.json` contains the matrix expression.
-   `./output.json` is where the result or error is written.

After execution, worker statistics are printed to the console.

## Testing

The project includes JUnit tests for core functionality, including:

-   Matrix addition, multiplication, transpose, and negation
-   Nested and chained computations
-   Dimension mismatch handling
-   Shared matrix and vector behavior
-   Executor and worker-thread behavior

Run the test suite with:

``` bash
mvn test
```

## Technologies

`Java` · `Multithreading` · `Synchronization` · `Maven` · `JUnit` ·
`JSON`
