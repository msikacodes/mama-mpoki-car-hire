export default function DataTable({ columns, data, emptyText = 'No data found' }) {
  if (!data || data.length === 0) {
    return <div className="empty">{emptyText}</div>;
  }
  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>
            {columns.map((col, i) => <th key={i}>{col.header}</th>)}
          </tr>
        </thead>
        <tbody>
          {data.map((row, ri) => (
            <tr key={ri}>
              {columns.map((col, ci) => (
                <td key={ci}>{col.render ? col.render(row) : row[col.accessor]}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
