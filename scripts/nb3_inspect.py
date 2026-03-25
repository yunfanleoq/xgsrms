#!/usr/bin/env python3
"""
Inspect Navicat MySQL .nb3 backup: list gzip/JSON table chunks (metadata).
Data rows are often in separate segments; full SQL export needs Navicat restore or mysqldump.

Usage:
  python scripts/nb3_inspect.py "C:\\path\\to\\file.nb3"
"""
from __future__ import annotations

import gzip
import json
import sys
from pathlib import Path


def first_ok_length(data: bytes, off: int, max_scan: int = 2_000_000) -> int | None:
    """Smallest L such that gzip.decompress(data[off:off+L]) succeeds (complete member)."""
    if off + 20 > len(data) or data[off : off + 2] != b"\x1f\x8b":
        return None
    hi_end = min(off + max_scan, len(data))
    # exponential search for upper bound that works
    lo_fail, hi_ok = 19, None
    step = 64
    L = step
    while L < hi_end - off:
        try:
            gzip.decompress(data[off : off + L])
            hi_ok = L
            break
        except Exception:
            lo_fail = L
            L = min(L * 2, hi_end - off)
    if hi_ok is None:
        return None
    # binary search minimal L in (lo_fail, hi_ok]
    lo, hi = lo_fail + 1, hi_ok
    while lo < hi:
        mid = (lo + hi) // 2
        try:
            gzip.decompress(data[off : off + mid])
            hi = mid
        except Exception:
            lo = mid + 1
    return lo


def iter_nb3_json_chunks(path: Path):
    data = path.read_bytes()
    off = 0
    seen = 0
    while off < len(data):
        p = data.find(b"\x1f\x8b\x08", off)
        if p < 0:
            break
        L = first_ok_length(data, p)
        if L is None:
            off = p + 2
            continue
        try:
            raw = gzip.decompress(data[p : p + L])
            if not raw or raw[0] != ord("{"):
                off = p + L
                continue
            j = json.loads(raw.decode("utf-8"))
        except Exception:
            off = p + L
            continue
        yield p, L, j
        seen += 1
        off = p + L


def main() -> int:
    if len(sys.argv) < 2:
        print(__doc__, file=sys.stderr)
        return 2
    path = Path(sys.argv[1])
    if not path.is_file():
        print("File not found:", path, file=sys.stderr)
        return 1
    tables = []
    for pos, L, j in iter_nb3_json_chunks(path):
        name = j.get("Name") or ""
        typ = j.get("Type") or ""
        if typ != "Table" and not name:
            continue
        rows = j.get("Data")
        nrows = len(rows) if isinstance(rows, list) else -1
        ddl = (j.get("DDL") or "")[:80]
        tables.append((name, typ, nrows, pos, L, ddl))

    print(f"file={path} size={path.stat().st_size} table_like_chunks={len(tables)}")
    for name, typ, nrows, pos, L, ddl in tables[:30]:
        print(f"  {name!r} type={typ!r} rows={nrows} off={pos} gz={L} ddl={ddl!r}...")
    qrtz = [t for t in tables if "qrtz" in t[0].lower()]
    print(f"\nQRTZ-related: {len(qrtz)}")
    for t in qrtz:
        print(f"  {t[0]} rows={t[2]}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
