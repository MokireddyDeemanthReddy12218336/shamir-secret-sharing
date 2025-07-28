# shamir-secret-sharing
# Shamir Secret Sharing Recovery

This project reconstructs the secret from multiple encoded shares using Lagrange Interpolation.

## How to Use

1. Ensure you have Java and `json-20220320.jar` library.
2. Use either `testcase1.json` or `testcase2.json` by updating the filename in `ShamirSecretRecovery.java`.
3. Compile:

```bash
javac -cp .;json-20220320.jar ShamirSecretRecovery.java
