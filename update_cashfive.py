import os
import csv
import urllib.request

CSV_PATH = "cashfive.csv"
TEXAS_LOTTERY_CSV_URL = "https://www.texaslottery.com/export/sites/lottery/Games/Cash_Five/Winning_Numbers/cashfive.csv"

def update_csv():
    print("Fetching latest Texas Cash 5 CSV from official Texas Lottery source...")
    try:
        req = urllib.request.Request(
            TEXAS_LOTTERY_CSV_URL,
            headers={'User-Agent': 'Mozilla/5.0'}
        )
        with urllib.request.urlopen(req) as response:
            remote_data = response.read().decode('utf-8').splitlines()
    except Exception as e:
        print(f"Failed to fetch remote CSV: {e}")
        return

    # Parse remote draws
    remote_draws = {}
    for line in remote_data:
        parts = line.split(',')
        if len(parts) >= 9 and "Cash Five" in parts[0]:
            try:
                m = int(parts[1].strip())
                d = int(parts[2].strip())
                y = int(parts[3].strip())
                n1 = int(parts[4].strip())
                n2 = int(parts[5].strip())
                n3 = int(parts[6].strip())
                n4 = int(parts[7].strip())
                n5 = int(parts[8].strip())
                remote_draws[(m, d, y)] = (n1, n2, n3, n4, n5)
            except ValueError:
                continue

    # Parse local draws
    local_draws = {}
    if os.path.exists(CSV_PATH):
        with open(CSV_PATH, mode='r', encoding='utf-8') as f:
            reader = csv.reader(f)
            for row in reader:
                if len(row) >= 9 and "Cash Five" in row[0]:
                    try:
                        m = int(row[1].strip())
                        d = int(row[2].strip())
                        y = int(row[3].strip())
                        n1 = int(row[4].strip())
                        n2 = int(row[5].strip())
                        n3 = int(row[6].strip())
                        n4 = int(row[7].strip())
                        n5 = int(row[8].strip())
                        local_draws[(m, d, y)] = (n1, n2, n3, n4, n5)
                    except ValueError:
                        continue

    # Find new draws
    new_draws = []
    for date_key, nums in remote_draws.items():
        if date_key not in local_draws:
            new_draws.append((date_key, nums))

    if not new_draws:
        print("CSV is already up to date. No new draws found.")
        return

    print(f"Found {len(new_draws)} new draws!")

    # Combine all draws and sort by date descending
    all_draws = {**local_draws, **remote_draws}
    sorted_draws = sorted(all_draws.items(), key=lambda x: (x[0][2], x[0][0], x[0][1]), reverse=True)

    # Write back to cashfive.csv
    with open(CSV_PATH, mode='w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        for (m, d, y), (n1, n2, n3, n4, n5) in sorted_draws:
            writer.writerow(["Cash Five", m, d, y, n1, n2, n3, n4, n5])

    print(f"Successfully updated {CSV_PATH} with {len(new_draws)} new draws.")

if __name__ == "__main__":
    update_csv()
