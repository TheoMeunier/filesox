export function TypoCode({ children }: { children: React.ReactNode }) {
    return <span className="text-sm text-indigo-500 font-mono bg-indigo-100 px-2 py-1 rounded-md">
        {children}
    </span>
}